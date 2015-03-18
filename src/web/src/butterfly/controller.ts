module Saturn.Butterfly {

    export interface IScope extends ng.IScope {
        columns: Saturn.OptionView.IColumnData[];
        data: ng.IPromise<any>;
        toSelectedItem: (d: any) => any;
    }

    export class Controller {
        public static $inject = ["$scope", "api.data.service"];
        constructor($scope: IScope, $data: Saturn.Api.Data.Service) {
            $scope.columns = [
                { data: "option.underlying.symbol", title: "Underlying", type: Saturn.OptionView.IColumnType.STRING },
                { data: "option.expiry", title: "Mid Expiry", type: Saturn.OptionView.IColumnType.DATE },
                { data: "low", title: "Low strike", type: Saturn.OptionView.IColumnType.NUMERIC },
                { data: "option.strike", title: "Mid strike", type: Saturn.OptionView.IColumnType.NUMERIC },
                { data: "high", title: "High strike", type: Saturn.OptionView.IColumnType.NUMERIC },
                { data: "setup", title: "Setup cost", type: Saturn.OptionView.IColumnType.NUMERIC },
                { data: "roi", title: "Roi", type: Saturn.OptionView.IColumnType.NUMERIC }
            ];
            $scope.data = $data.getButterfly();
        }
    }

    controllers.controller("saturn.butterfly.controller", Controller);
}