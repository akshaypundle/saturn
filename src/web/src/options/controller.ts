module Saturn.Options {

    export interface IDTOptionsBuilder {
        fromSource(data: string): IDTOptionsBuilder;
        fromFnPromise(promiseFn: () => ng.IPromise<any>): IDTOptionsBuilder;
        withPaginationType(paginationType: string): IDTOptionsBuilder;
        withOption(key: any, value: any): IDTOptionsBuilder;
        withBootstrap(): IDTOptionsBuilder;
    }

    export interface IDTColumnBuilder {
        newColumn(name: string): IDTColumnBuilder;
        withTitle(title: string): IDTColumnBuilder;
    }

    export interface IScope extends ng.IScope {
        options: any;
        columns: any[];
    }

    export class Controller {
        public static $inject = ["$scope", "DTOptionsBuilder", "DTColumnBuilder", "$q"];
        constructor($scope: IScope, DTOptionsBuilder: IDTOptionsBuilder, DTColumnBuilder: IDTColumnBuilder, $q: ng.IQService) {
            $scope.options = DTOptionsBuilder.fromFnPromise(() => $q.when(options))
                .withOption("lengthChange", false)
                .withBootstrap();
            $scope.columns = [
                DTColumnBuilder.newColumn("option.underlying.symbol").withTitle("Underlying"),
                DTColumnBuilder.newColumn("option.expiry").withTitle("Expiry"),
                DTColumnBuilder.newColumn("option.strike").withTitle("Strike"),
                DTColumnBuilder.newColumn("option.type").withTitle("Type"),
                DTColumnBuilder.newColumn("option.bid").withTitle("Bid"),
            ];
        }
    }

    controllers.controller("options.controller", Controller);
}