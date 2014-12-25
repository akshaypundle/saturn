module Saturn.ShortPuts {

    export interface IScope extends ng.IScope {
        columns: Saturn.OptionView.IColumnData[];
        data: ng.IPromise<any>;
    }

    export class Controller {
        public static $inject = ["$scope", "api.data.service"];
        constructor($scope: IScope, $data: Saturn.Api.Data.Service) {
            $scope.columns = [
                { id: "option.underlying.symbol", title: "Underlying", type: Saturn.OptionView.IColumnType.STRING },
                { id: "shortPutRoi", title: "Roi", type: Saturn.OptionView.IColumnType.NUMERIC },
                { id: "shortPutDownProtect", title: "Protect", type: Saturn.OptionView.IColumnType.NUMERIC },
                { id: "option.expiry", title: "Expiry", type: Saturn.OptionView.IColumnType.DATE },
                { id: "option.strike", title: "Strike", type: Saturn.OptionView.IColumnType.NUMERIC },
                { id: "option.type", title: "Type", type: Saturn.OptionView.IColumnType.OPTION_TYPE },
                { id: "option.bid", title: "Bid", type: Saturn.OptionView.IColumnType.NUMERIC }
            ];
            $scope.data = $data.getShortPuts();
        }
    }

    controllers.controller("saturn.shortPuts.controller", Controller);
}