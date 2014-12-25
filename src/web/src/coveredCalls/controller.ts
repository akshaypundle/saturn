module Saturn.CoveredCalls {

    export interface IScope extends ng.IScope {
        columns: Saturn.OptionView.IColumnData[];
        data: ng.IPromise<any>;
    }

    export class Controller {
        public static $inject = ["$scope", "api.data.service"];
        constructor($scope: IScope, $data: Saturn.Api.Data.Service) {
            $scope.columns = [
                { id: "option.underlying.symbol", title: "Underlying", type: Saturn.OptionView.IColumnType.STRING },
                { id: "coveredCallRoi", title: "Roi", type: Saturn.OptionView.IColumnType.NUMERIC },
                { id: "coveredCallDownProtect", title: "Protect", type: Saturn.OptionView.IColumnType.NUMERIC },
                { id: "option.expiry", title: "Expiry", type: Saturn.OptionView.IColumnType.DATE },
                { id: "option.strike", title: "Strike", type: Saturn.OptionView.IColumnType.NUMERIC },
                { id: "option.type", title: "Type", type: Saturn.OptionView.IColumnType.OPTION_TYPE },
                { id: "option.bid", title: "Bid", type: Saturn.OptionView.IColumnType.NUMERIC }
            ];
            $scope.data = $data.getCoveredCalls();
        }
    }

    controllers.controller("saturn.coveredCalls.controller", Controller);
}