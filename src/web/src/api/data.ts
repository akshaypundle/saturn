
module Saturn.Api.Data {

    export class Service {
        private $q: ng.IQService;
        private $http: ng.IHttpService;
        private cache: any[];

        public static $inject = ["$q", "$http"];
        constructor($q: ng.IQService, $http: ng.IHttpService) {
            this.$q = $q;
            this.$http = $http;
            this.cache = [null, null, null, null];
        }

        public getOptions() {
            return this.memoized(this.$http.get("options.js"), 0);
        }

        public getCoveredCalls() {
            return this.memoized(this.$http.get("coveredCalls.js"), 1);
        }

        public getShortPuts() {
            return this.memoized(this.$http.get("shortPuts.js"), 2);
        }

        public getButterfly() {
            return this.memoized(this.$http.get("butterfly.js"), 3);
        }

        private memoized(promise: ng.IPromise<any>, cacheIndex: number) {
            if (this.cache[cacheIndex]) {
                return this.$q.when(this.cache[cacheIndex]);
            } else {
                return promise.then((d) => {
                    this.cache[cacheIndex] = d.data;
                    return d.data;
                });
            }
        }
    }
    services.service("api.data.service", Service);
}