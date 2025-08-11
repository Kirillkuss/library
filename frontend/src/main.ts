import { bootstrapApplication } from '@angular/platform-browser';
import { App } from './app/app';  // Измените Book на App
import { config } from './app/app.config.server';

const bootstrap = () => bootstrapApplication(App, config);  // Используйте App

export default bootstrap;
