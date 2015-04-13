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
                { data: "option.underlying.bid", title: "Price", type: Saturn.OptionView.IColumnType.STRING },
                { data: "option.expiry", title: "Expiry", type: Saturn.OptionView.IColumnType.DATE },
                { data: "low", title: "Low", type: Saturn.OptionView.IColumnType.NUMERIC },
                { data: "option.strike", title: "Mid", type: Saturn.OptionView.IColumnType.NUMERIC },
                { data: "high", title: "High", type: Saturn.OptionView.IColumnType.NUMERIC },
                { data: "setup", title: "Cost $", type: Saturn.OptionView.IColumnType.NUMERIC },
                { data: "roi", title: "Roi $", type: Saturn.OptionView.IColumnType.NUMERIC },
                { data: "zero", title: "Zero %", type: Saturn.OptionView.IColumnType.NUMERIC, render: Util.renderPercent }

            ];
            $scope.data = $data.getButterfly();
        }
    }

    controllers.controller("saturn.butterfly.controller", Controller);
}