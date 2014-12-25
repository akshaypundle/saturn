
module Saturn.Api.Data {

    export class Service {
        private $q: ng.IQService;
        private $http: ng.IHttpService;

        public static $inject = ["$q", "$http"];
        constructor($q: ng.IQService, $http: ng.IHttpService) {
            this.$q = $q;
            this.$http = $http;
        }

        public getOptions() {
            return this.$http.get("options.js").then((d) => {
                return d.data;
            });
        }

        public getCoveredCalls() {
            return this.$http.get("coveredCalls.js").then((d) => {
                return d.data;
            });
        }

        public getShortPuts() {
            return this.$http.get("shortPuts.js").then((d) => {
                return d.data;
            });
        }
    }
    services.service("api.data.service", Service);
}