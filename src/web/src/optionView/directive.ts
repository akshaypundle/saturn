module Saturn.OptionView {

    export class Directive implements ng.IDirective {
        public controller = "saturn.optionView.controller";
        public restrict = "E";
        public templateUrl = "src/optionView/template.html";
        public scope = {
            columns: "=",
            data: "=",
            title: "@"
        };
        private $timeout: ng.ITimeoutService;
        private hotkeys: ng.hotkeys.HotkeysProvider;

        public static $inject = ["$timeout", "hotkeys"];
        constructor($timeout: ng.ITimeoutService, hotkeys: ng.hotkeys.HotkeysProvider) {
            this.$timeout = $timeout;
            this.hotkeys = hotkeys;
        }

        public link = ($scope: Saturn.OptionView.IScope, element: JQuery) => {
            var tableElement = element.find(".main-table");
            $scope.dataLoaded = false;

            for (var i = 0; i < $scope.columns.length; i++) {
                var col = $scope.columns[i];
                if (col.type === IColumnType.NUMERIC && !col.render) {
                    col.render = Util.render2Decimals;
                }
            };

            $scope.dataTable = this.createTable(tableElement, [], $scope.columns);
            this.initNumericFilters($scope);
            this.initSelection($scope, tableElement);

            // use timeout to delay adding data so that the "Loading..." message
            // gets displayed
            this.$timeout(() =>
                $scope.data.then((d) => {
                    $scope.dataTable.rows.add(d);
                    $scope.dataTable.draw();
                }).finally(() => {
                    $scope.dataLoaded = true;
                }), 0);

            this.hotkeys.bindTo($scope).add({
                combo: "right",
                description: "next page",
                callback: () => {
                    $scope.dataTable.page("next");
                    $scope.dataTable.draw(false);

                }
            }).add({
                combo: "left",
                description: "previous page",
                callback: () => {
                    $scope.dataTable.page("previous");
                    $scope.dataTable.draw(false);
                }
            });
        };

        private createTable(tableElement: JQuery, data: any, columns: any[]) {
            var initOptions: any = {
                data: data,
                columns: columns,
                dom: "ftipr",
                processing: true,
                deferRender: true
            };

            return tableElement.DataTable(initOptions);
        }

        private initNumericFilters($scope: IScope) {

            $scope.numericColumns = [];
            for (var i = 0; i < $scope.columns.length; i++) {
                var col = $scope.columns[i];
                if (col.type !== IColumnType.NUMERIC) {
                    continue;
                }
                $scope.numericColumns.push({ min: col.defaultMin, max: col.defaultMax, index: i, title: col.title });
            };

            $scope.$on("$destroy", () => {
                $.fn.dataTable.ext.search.pop();
            });

            $.fn.dataTable.ext.search.push((settings: any, data: any, dataIndex: any) => {
                for (var i = 0; i < $scope.numericColumns.length; i++) {
                    var col = $scope.numericColumns[i];
                    var datum = parseFloat(data[col.index]) || undefined;

                    if (col.min !== null && !isNaN(col.min) && (datum === undefined || datum < col.min)) {
                        return false;
                    }
                    if (col.max !== null && !isNaN(col.max) && (datum === undefined || datum > col.max)) {
                        return false;
                    }
                }
                return true;
            });

            $scope.$watch("numericColumns", () => {
                if ($scope.dataTable) {
                    $scope.dataTable.draw(false);
                }
            }, true);
        }

        private initSelection($scope: IScope, tableElement: JQuery) {
            tableElement.on("click", "tr", (event: JQueryEventObject) => {
                var currentRowElement = $(event.currentTarget);

                if (currentRowElement.hasClass("selected")) {
                    $scope.dataTable.$("tr.selected").removeClass("selected");

                    $scope.$apply(() => {
                        $scope.updateSelectedOptionDetails(null);
                    });
                } else {
                    $scope.dataTable.$("tr.selected").removeClass("selected");
                    var row = $scope.dataTable.row(currentRowElement);
                    if (row) {
                        var node = row.node();
                        $(node).addClass("selected");
                        $scope.$apply(() => {
                            var data : any = row.data();
                            if (data) {
                                $scope.updateSelectedOptionDetails(data.option.underlying.symbol);
                            }
                        });
                    }
                }
            });
        }
    }
    directives.directive("saturn.optionView.directive", ["$timeout", "hotkeys",
        ($timeout: ng.ITimeoutService, hotkeys: ng.hotkeys.HotkeysProvider) => new Saturn.OptionView.Directive($timeout, hotkeys)]);
}