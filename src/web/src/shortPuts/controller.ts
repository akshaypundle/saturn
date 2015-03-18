module Saturn.ShortPuts {

    export interface IScope extends ng.IScope {
        columns: Saturn.OptionView.IColumnData[];
        data: ng.IPromise<any>;
    }

    export class Controller {
        public static $inject = ["$scope", "api.data.service"];
        constructor($scope: IScope, $data: Saturn.Api.Data.Service) {
            $scope.columns = [
                { data: "option.underlying.symbol", title: "Underlying", type: Saturn.OptionView.IColumnType.STRING },
                { data: "shortPutRoi", title: "Roi %", type: Saturn.OptionView.IColumnType.NUMERIC, defaultMin: 1, defaultMax: 4,
                    render: Util.renderPercent },
                { data: "shortPutDownProtect", title: "Protect %", type: Saturn.OptionView.IColumnType.NUMERIC, defaultMin: 0,
                    render: Util.renderPercent },
                { data: "option.expiry", title: "Expiry", type: Saturn.OptionView.IColumnType.DATE },
                { data: "option.strike", title: "Strike", type: Saturn.OptionView.IColumnType.NUMERIC },
                { data: "option.type", title: "Type", type: Saturn.OptionView.IColumnType.OPTION_TYPE },
                { data: "option.bid", title: "Bid", type: Saturn.OptionView.IColumnType.NUMERIC }
            ];
            $scope.data = $data.getShortPuts();
        }
    }

    controllers.controller("saturn.shortPuts.controller", Controller);
}