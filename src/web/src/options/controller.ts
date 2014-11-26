module Saturn.Options {

    export interface IDTOptionsBuilder {
        fromSource(data: string): IDTOptionsBuilder;
        fromFnPromise(promiseFn: () => ng.IPromise<any>): IDTOptionsBuilder;
        withPaginationType(paginationType: string): IDTOptionsBuilder;
        withOption(key: any, value: any): IDTOptionsBuilder;
        withBootstrap(): IDTOptionsBuilder;
        withTableTools(swfPath: string): IDTOptionsBuilder;
        withTableToolsButtons(param: any): IDTOptionsBuilder;
    }

    export interface IDTColumnBuilder {
        newColumn(name: string): IDTColumnBuilder;
        withTitle(title: string): IDTColumnBuilder;
    }

    export interface IScope extends ng.IScope {
        selected: string;
        selectedQuote: any;
        options: any;
        columns: any[];
    }

    export class Controller {
        public static $inject = ["$scope", "DTOptionsBuilder", "DTColumnBuilder", "$q", "$http", "$resource"];
        constructor($scope: IScope,
            DTOptionsBuilder: IDTOptionsBuilder,
            DTColumnBuilder: IDTColumnBuilder,
            $q: ng.IQService,
            $http: ng.IHttpService,
            $resource: ng.resource.IResourceService) {

            var rowSelectionCallback = (nodes: any) => {
                $scope.$apply(() => {
                    $scope.selected = nodes[0].firstChild.innerText;
                });

                var request = $http.get("https://query.yahooapis.com/v1/public/yql?q=" +
                    "select%20*%20from%20yahoo.finance.quotes%20where%20symbol%20in%20(%22" + $scope.selected +
                    "%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");

                request.then((quote: any) => {
                    $scope.selectedQuote = quote.data.query.results.quote;
                });
            };

            var tableToolsOptions = {
                "sSwfPath": "libraries/datatables-tabletools/swf/copy_csv_xls_pdf.swf",
                "sRowSelect": "single",
                "aButtons": <String[]>[],
                "fnRowSelected": rowSelectionCallback
            };

            $scope.options = DTOptionsBuilder.fromFnPromise(() => $q.when(options))
                .withOption("lengthChange", false)
                .withOption("tableTools", tableToolsOptions)
                .withTableTools("libraries/datatables-tabletools/swf/copy_csv_xls_pdf.swf")
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
    controllers.controller("saturn.options.controller", Controller);
}