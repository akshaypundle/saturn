module Saturn {
    export var controllers = angular.module("saturn.controllers", []);
    export var directives = angular.module("saturn.directives", []);
    export var services = angular.module("saturn.services", []);

    export var app = angular.module("saturn.app", [
        controllers.name,
        directives.name,
        services.name,
        "ui.router"
    ]);

    app.config(($stateProvider: ng.ui.IStateProvider, $urlRouterProvider: ng.ui.IUrlRouterProvider) => {
        $urlRouterProvider.otherwise("");
        $stateProvider.state("/", {
            url: "/",
            templateUrl: "src/partials/saturn.html"
        }).state("help", {
            url: "/help",
            templateUrl: "src/partials/help.html"
        }).state("options", {
            url: "/options",
            templateUrl: "src/options/partial.html"
        }).state("covered-calls", {
            url: "/covered-calls",
            templateUrl: "src/coveredCalls/partial.html"
        }).state("short-puts", {
            url: "/short-puts",
            templateUrl: "src/shortPuts/partial.html"
        }).state("butterfly", {
            url: "/butterfly",
            templateUrl: "src/partials/butterfly.html"
        });
    });
}