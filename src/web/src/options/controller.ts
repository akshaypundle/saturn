module Saturn.Options {

    export interface IScope extends ng.IScope {
        selected: string;
        selectedQuote: any;
        options: any;
        columns: any[];
        numericColumns: INumericFilter[];
    }

    export enum IColumnType {
        NUMERIC, STRING, DATE, OPTION_TYPE
    };

    export interface IColumnData {
        id: string;
        title: string;
        type: IColumnType;
    };

    export interface INumericFilter {
        index: number;
        min: number;
        max: number;
        title: string;
    }

    export class Controller {
        private underlyingIndex = 0;
        private columns: IColumnData[] = [
            { id: "option.underlying.symbol", title: "Underlying", type: IColumnType.STRING },
            { id: "option.expiry", title: "Expiry", type: IColumnType.DATE },
            { id: "option.strike", title: "Strike", type: IColumnType.NUMERIC },
            { id: "option.type", title: "Type", type: IColumnType.OPTION_TYPE },
            { id: "option.bid", title: "Bid", type: IColumnType.NUMERIC }
        ];
        private dataTable: any;

        public static $inject = ["$scope", "DTOptionsBuilder", "DTColumnBuilder", "$q", "$http"];
        constructor($scope: IScope,
            DTOptionsBuilder: jquery.dataTables.IDTOptionsBuilder,
            DTColumnBuilder: jquery.dataTables.IDTColumnBuilder,
            $q: ng.IQService,
            $http: ng.IHttpService) {

            var rowSelectionCallback = (nodes: any) => {
                $scope.$apply(() => {
                    $scope.selected = nodes[this.underlyingIndex].firstChild.innerText;
                });

                var request = $http.get("https://query.yahooapis.com/v1/public/yql?q=" +
                    "select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22" + $scope.selected +
                    "%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");

                request.then((quote: any) => {
                    $scope.selectedQuote = quote.data.query.results.quote;
                });
            };

            var tableToolsOptions = {
                "sRowSelect": "single",
                "aButtons": <String[]>[],
                "fnRowSelected": rowSelectionCallback
            };

            $scope.options = DTOptionsBuilder.fromFnPromise(() => $q.when(options))
                .withOption("lengthChange", false)
                .withOption("tableTools", tableToolsOptions)
                .withTableTools("")
                .withBootstrap();

            $scope.columns = this.columns.map((col) => DTColumnBuilder.newColumn(col.id).withTitle(col.title));
            $scope.numericColumns = [];
            for (var i = 0; i < this.columns.length; i++) {
                var col = this.columns[i];
                if (col.type !== IColumnType.NUMERIC) {
                    continue;
                }

                $scope.numericColumns.push({ min: <number>undefined, max: <number>undefined, index: i, title: col.title });
            };

            $.fn.dataTable.ext.search.push((settings: any, data: any, dataIndex: any) => {
                for (var i = 0; i < $scope.numericColumns.length; i++) {
                    var col = $scope.numericColumns[i];
                    var datum = parseFloat(data[col.index]) || undefined;
                    if (col.min !== undefined && datum !== undefined && datum < col.min) {
                        return false;
                    }
                    if (col.max !== undefined && datum !== undefined && datum > col.max) {
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
    controllers.controller("saturn.options.controller", Controller);
}