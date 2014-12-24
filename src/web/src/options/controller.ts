module Saturn.Options {

    export interface IScope extends ng.IScope {
        columns: Saturn.OptionView.IColumnData[];
        data: ng.IPromise<any>;
    }

    export class Controller {
        public static $inject = ["$scope", "api.data.service"];
        constructor($scope: IScope, $data: Saturn.Api.Data.Service) {
            $scope.columns = [
                { id: "underlying.symbol", title: "Underlying", type: Saturn.OptionView.IColumnType.STRING },
                { id: "expiry", title: "Expiry", type: Saturn.OptionView.IColumnType.DATE },
                { id: "strike", title: "Strike", type: Saturn.OptionView.IColumnType.NUMERIC },
                { id: "type", title: "Type", type: Saturn.OptionView.IColumnType.OPTION_TYPE },
                { id: "bid", title: "Bid", type: Saturn.OptionView.IColumnType.NUMERIC }
            ];
            $scope.data = $data.getOptions();
        }
    }

    controllers.controller("saturn.options.controller", Controller);
}