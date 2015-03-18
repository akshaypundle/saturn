module Saturn.OptionView {

    export enum IColumnType {
        NUMERIC, STRING, DATE, OPTION_TYPE
    };

    export interface IColumnData {
        data: string;
        title: string;
        type: IColumnType;
        defaultMin?: number;
        defaultMax?: number;
        render?: any;
    };

    export interface IScope extends ng.IScope {
        columns: IColumnData[];
        data: ng.IPromise<any>;
        dataTable: DataTables.DataTable;
        dataLoaded: boolean;
        numericColumns: INumericFilter[];
        selectedOptionDetails: any;
        updateSelectedOptionDetails: (underlying: string) => any;
    }

    interface INumericFilter {
        index: number;
        min: number;
        max: number;
        title: string;
    }

    export class Controller {
        private $http: ng.IHttpService;
        private $scope : IScope;

        public static $inject = ["$scope", "$http"];
        constructor($scope: IScope, $http: ng.IHttpService) {
            this.$scope = $scope;
            this.$http = $http;

            $scope.updateSelectedOptionDetails = (underlying: string) => {
                if (underlying) {
                    var request = this.$http.get("https://query.yahooapis.com/v1/public/yql?q=" +
                        "select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22" + underlying +
                        "%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");

                    request.then((quote: any) => {
                        this.$scope.selectedOptionDetails = quote.data.query.results.quote;
                    });
                }
            };
        }
    }
    controllers.controller("saturn.optionView.controller", Controller);
}