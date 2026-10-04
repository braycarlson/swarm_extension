from __future__ import annotations

import os
import shutil
import subprocess
import sys
import threading

import sublime
import sublime_plugin

from pathlib import Path


SETTINGS_NAME = 'swarm.sublime-settings'


def executable_name() -> str:
    if sys.platform == 'win32':
        return 'swarm-full.exe'

    return 'swarm-full'


def install_directory() -> Path:
    configured = os.environ.get('SWARM_INSTALL_DIR')

    if configured:
        return Path(configured)

    return Path.home().joinpath('stratus', 'swarm')


def newest_build(source: Path, name: str) -> Path | None:
    target = source.joinpath('target')

    candidates = [
        *target.glob(f'*/release/{name}'),
        target.joinpath('release', name),
    ]

    builds = [candidate for candidate in candidates if candidate.is_file()]

    if not builds:
        return None

    return max(builds, key=lambda build: build.stat().st_mtime)


class SwarmCommand(sublime_plugin.WindowCommand):
    def _executable(self) -> Path | None:
        settings = sublime.load_settings(SETTINGS_NAME)
        configured = settings.get('executable', '')
        name = executable_name()

        if configured:
            explicit = Path(configured).expanduser()

            if explicit.is_file():
                return explicit

        for source in settings.get('sources', []):
            build = newest_build(Path(source).expanduser(), name)

            if build is not None:
                return build

        installed = install_directory().joinpath(name)

        if installed.is_file():
            return installed

        found = shutil.which('swarm-full')

        if found is None:
            return None

        return Path(found)

    def _target(self, paths: list[str]) -> str | None:
        if paths:
            return paths[0]

        view = self.window.active_view()

        if view is None:
            return None

        return view.file_name()

    def is_visible(self, paths: list[str] | None = None) -> bool:
        return self._target(paths or []) is not None

    def run(self, paths: list[str] | None = None) -> None:
        file_path = self._target(paths or [])

        if file_path is None:
            sublime.message_dialog('No file selected or open!')

            return

        executable = self._executable()

        if executable is None:
            sublime.message_dialog('swarm-full binary not found')

            return

        process = subprocess.Popen(
            [str(executable), file_path],
            stdin=subprocess.DEVNULL,
            stdout=subprocess.DEVNULL,
            stderr=subprocess.DEVNULL,
        )

        threading.Thread(target=process.wait, name='swarm-reaper', daemon=True).start()
