module Saturn.OptionView {

    export enum IColumnType {
        NUMERIC, STRING, DATE, OPTION_TYPE
    };

    export interface IColumnData {
        id: string;
        title: string;
        type: IColumnType;
    };

    interface IScope extends ng.IScope {
        columns: IColumnData[];
        data: ng.IPromise<any>;
        dtOptions: any;
        dtColumns: any[];
        numericColumns: INumericFilter[];
        selectedOption: string;
        selectedOptionDetails: any;
    }

    interface INumericFilter {
        index: number;
        min: number;
        max: number;
        title: string;
    }

    export class Controller {
        private underlyingIndex = 0;
        private dataTable: any;

        public static $inject = ["$scope", "DTOptionsBuilder", "DTColumnBuilder", "$http"];
        constructor($scope: IScope,
            DTOptionsBuilder: jquery.dataTables.IDTOptionsBuilder,
            DTColumnBuilder: jquery.dataTables.IDTColumnBuilder,
            $http: ng.IHttpService) {

            var rowSelectionCallback = (nodes: any) => {
                $scope.$apply(() => {
                    $scope.selectedOption = nodes[this.underlyingIndex].firstChild.innerText;
                });

                var request = $http.get("https://query.yahooapis.com/v1/public/yql?q=" +
                    "select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22" + $scope.selectedOption +
                    "%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");

                request.then((quote: any) => {
                    $scope.selectedOptionDetails = quote.data.query.results.quote;
                });
            };

            var tableToolsOptions = {
                sRowSelect: "single",
                aButtons: <String[]>[],
                fnRowSelected: rowSelectionCallback
            };

            $scope.dtOptions = DTOptionsBuilder.fromFnPromise(() => $scope.data)
                .withOption("lengthChange", false)
                .withOption("hover", true)
                .withOption("tableTools", tableToolsOptions)
                .withTableTools("")
                .withBootstrap();

            $scope.dtColumns = $scope.columns.map((col) => DTColumnBuilder.newColumn(col.id).withTitle(col.title));
            $scope.numericColumns = [];
            for (var i = 0; i < $scope.columns.length; i++) {
                var col = $scope.columns[i];
                if (col.type !== IColumnType.NUMERIC) {
                    continue;
                }

                $scope.numericColumns.push({ min: <number>undefined, max: <number>undefined, index: i, title: col.title });
            };

            $.fn.dataTable.ext.search.push((settings: any, data: any, dataIndex: any) => {
                for (var i = 0; i < $scope.numericColumns.length; i++) {
                    var col = $scope.numericColumns[i];
                    var datum = parseFloat(data[col.index]) || undefined;
                    if (col.min && datum !== undefined && datum < col.min) {
                        return false;
                    }
                    if (col.max && datum !== undefined && datum > col.max) {
                        return false;
                    }
                }
                return true;
            });

            $scope.$on("event:dataTableLoaded", (event, loadedDT) => {
                this.dataTable = loadedDT.DataTable;
            });

            $scope.$watch("numericColumns", () => {
                if (this.dataTable) {
                    this.dataTable.draw();
                }
            }, true);
        }
    }
    controllers.controller("saturn.optionView.controller", Controller);
}