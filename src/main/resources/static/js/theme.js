/**
 * Theme Manager Module
 * Handles Light / Dark theme toggle and localStorage persistence
 */

const ThemeManager = {
    STORAGE_KEY: 'feedback_flow_theme',

    init() {
        const savedTheme = localStorage.getItem(this.STORAGE_KEY) || 'light';
        this.setTheme(savedTheme);

        const themeToggleBtn = document.getElementById('theme-toggle-btn');
        if (themeToggleBtn) {
            themeToggleBtn.addEventListener('click', () => this.toggleTheme());
        }
    },

    setTheme(theme) {
        document.documentElement.setAttribute('data-theme', theme);
        localStorage.setItem(this.STORAGE_KEY, theme);

        const lightIcon = document.querySelector('#theme-toggle-btn .light-icon');
        const darkIcon = document.querySelector('#theme-toggle-btn .dark-icon');

        if (lightIcon && darkIcon) {
            if (theme === 'dark') {
                lightIcon.classList.add('hidden');
                darkIcon.classList.remove('hidden');
            } else {
                lightIcon.classList.remove('hidden');
                darkIcon.classList.add('hidden');
            }
        }
    },

    toggleTheme() {
        const currentTheme = document.documentElement.getAttribute('data-theme') || 'light';
        const newTheme = currentTheme === 'light' ? 'dark' : 'light';
        this.setTheme(newTheme);
    }
};

document.addEventListener('DOMContentLoaded', () => ThemeManager.init());
window.ThemeManager = ThemeManager;
