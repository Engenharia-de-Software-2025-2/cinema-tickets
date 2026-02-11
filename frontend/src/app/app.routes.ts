import { Routes } from '@angular/router';
import { Login } from '../components/login/login';
import { Home } from '../components/home/home';
import { authGuard } from '../auth/guard/auth-guard';

export const routes: Routes = [
    {
        path: '',
        component: Login,
    },
    {
        path: 'home',
        component: Home,
        //canActivate: [authGuard]
    }
];
