// components/ThemeSwitcher.tsx
import { useState, useEffect } from 'react';
import { LIGHT_THEME, DARK_THEME } from '../constants/theme'; // Import constants

interface ThemeSwitcherProps {
    onThemeChange: (theme: typeof LIGHT_THEME | typeof DARK_THEME) => void;
}

function ThemeSwitcher({ onThemeChange }: ThemeSwitcherProps) {
    const [theme, setTheme] = useState<typeof LIGHT_THEME | typeof DARK_THEME>(LIGHT_THEME);

    useEffect(() => {
        const prefersDarkScheme = window.matchMedia("(prefers-color-scheme: dark)").matches;
        const savedTheme = localStorage.getItem("theme") || (prefersDarkScheme ? DARK_THEME : LIGHT_THEME);
        setTheme(savedTheme as typeof LIGHT_THEME | typeof DARK_THEME);
        document.documentElement.setAttribute("data-theme", savedTheme);
        onThemeChange(savedTheme as typeof LIGHT_THEME | typeof DARK_THEME); // Pass the initial theme
    }, [onThemeChange]);

    const toggleTheme = () => {
        const newTheme = theme === LIGHT_THEME ? DARK_THEME : LIGHT_THEME;
        setTheme(newTheme);
        localStorage.setItem("theme", newTheme);
        document.documentElement.setAttribute("data-theme", newTheme);
        onThemeChange(newTheme);
    };

    return (
        <button className="theme-toggle" onClick={toggleTheme}>
            {theme === LIGHT_THEME ? "🌙 Dark Mode" : "☀️ Light Mode"}
        </button>
    );
}

export default ThemeSwitcher;