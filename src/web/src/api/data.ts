var options: Saturn.Api.Option[];
var coveredCalls: Saturn.Api.CoveredCall[];

module Saturn.Api.Data {

    export class Service {
        private $q: ng.IQService;

        public static $inject = ["$q"];
        constructor($q: ng.IQService) {
            this.$q = $q;
        }

        public getOptions() {
            return this.$q.when(options);
        }

        public getCoveredCalls() {
            return this.$q.when(coveredCalls);
        }
    }
    services.service("api.data.service", Service);
}