var Saturn;
(function (Saturn) {
    Saturn.controllers = angular.module("saturn.controllers", []);
    Saturn.directives = angular.module("saturn.directives", []);
    Saturn.services = angular.module("saturn.services", []);

    Saturn.app = angular.module("saturn.app", [
        Saturn.controllers.name,
        Saturn.directives.name,
        Saturn.services.name
    ]);
})(Saturn || (Saturn = {}));
//# sourceMappingURL=app.js.map
