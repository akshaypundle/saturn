module Saturn {
    export var controllers = angular.module("saturn.controllers", []);
    export var directives = angular.module("saturn.directives", []);
    export var services = angular.module("saturn.services", []);

    export var app = angular.module("saturn.app", [
        controllers.name,
        directives.name,
        services.name
    ]);
}