export class RouterPath {
    public static readonly main: string = 'main';
    public static readonly login: string = 'login';
    public static readonly registration: string = 'registration';
    public static readonly detail: string = 'detail';
    public static readonly admin: string = 'admin';
    public static readonly profil: string = 'profil';

    public static readonly guestRoutes: Array<string> = new Array<string>(
        RouterPath.login,
        RouterPath.registration
    );

    public static readonly userRoutes: Array<string> = new Array<string>(
        RouterPath.main,
        RouterPath.detail,
        RouterPath.profil
    );

    public static readonly adminRoutes: Array<string> = new Array<string>(
        RouterPath.login,
        RouterPath.registration,
        RouterPath.main,
        RouterPath.detail,
        RouterPath.admin,
        RouterPath.profil
    );
}
