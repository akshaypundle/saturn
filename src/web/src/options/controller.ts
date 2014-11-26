module Saturn.Options {

    export interface IDTOptionsBuilder {
        fromSource(data: string): IDTOptionsBuilder;
        fromFnPromise(promiseFn: () => ng.IPromise<any>): IDTOptionsBuilder;
        withPaginationType(paginationType: string): IDTOptionsBuilder;
        withOption(key: any, value: any): IDTOptionsBuilder;
        withBootstrap(): IDTOptionsBuilder;
        withTableTools(swfPath : string): IDTOptionsBuilder;
        withTableToolsButtons(param : any): IDTOptionsBuilder;
    }

    export interface IDTColumnBuilder {
        newColumn(name: string): IDTColumnBuilder;
        withTitle(title: string): IDTColumnBuilder;
    }

    export interface IScope extends ng.IScope {
        selected: string;
        options: any;
        columns: any[];
    }

    export class Controller {
        public static $inject = ["$scope", "DTOptionsBuilder", "DTColumnBuilder", "$q"];
        constructor($scope: IScope, DTOptionsBuilder: IDTOptionsBuilder, DTColumnBuilder: IDTColumnBuilder, $q: ng.IQService) {
            $scope.options = DTOptionsBuilder.fromFnPromise(() => $q.when(options))
                .withOption("lengthChange", false)
                .withOption("tableTools", {
                        "sSwfPath": "libraries/datatables-tabletools/swf/copy_csv_xls_pdf.swf",
                        "sRowSelect": "single",
                        "aButtons": ["copy", {
                                "sExtends": "collection",
                                "sButtonText": "Save",
                                "aButtons": ["csv", "pdf"]
                            }
                        ],
                        "fnRowSelected": function ( nodes : any ) {
                            $scope.$apply(() => {
                                $scope.selected = nodes[0].firstChild.innerText;
                            });
                        }
                    }
                )
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
    // chart : http://chart.finance.yahoo.com/z?s=GOOG&t=5d
    controllers.controller("saturn.options.controller", Controller);
}