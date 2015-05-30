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
            }).add({
                combo: "down",
                description: "next row",
                callback: () => {
                    var $rows = $scope.dataTable.rows({ page: "current" });
                    var $row = $scope.dataTable.rows({ page: "current" }).$("tr.selected");
                    var index = $row.index();
                    var row: DataTables.RowMethods;

                    if (index === $rows.indexes().length - 1 && $scope.dataTable.page() === $scope.dataTable.page.info().pages - 1) {
                        return; // nothing to do
                    }
                    Directive.unselectAllRows($scope);

                    if (index === $scope.dataTable.page.len() - 1) {
                        $scope.dataTable.page("next");
                        $scope.dataTable.draw(false);
                        row = $scope.dataTable.row($scope.dataTable.rows({ page: "current" }).pluck(0));
                    } else {
                        row = $scope.dataTable.row($scope.dataTable.rows({ page: "current" }).pluck(index + 1));
                    }
                    Directive.selectRow(row, $scope);
                    $scope.dataTable.draw(false);
                }
            }).add({
                combo: "up",
                description: "previous row",
                callback: () => {
                    var $row = $scope.dataTable.rows({ page: "current" }).$("tr.selected");
                    var index = $row.index();

                    if (index === 0 && $scope.dataTable.page() === 0) {
                        return; // nothing to do
                    }

                    Directive.unselectAllRows($scope);
                    var row: DataTables.RowMethods;
                    if (index === 0) {
                        $scope.dataTable.page("previous");
                        $scope.dataTable.draw(false);
                        row = $scope.dataTable.row($scope.dataTable.rows({ page: "current" }).pluck($scope.dataTable.page.len() - 1));
                    } else {
                        row = $scope.dataTable.row($scope.dataTable.rows({ page: "current" }).pluck(index - 1));
                    }
                    Directive.selectRow(row, $scope);
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

                $scope.$apply(() => {
                    if (currentRowElement.hasClass("selected")) {
                        Directive.unselectAllRows($scope);
                        $scope.updateSelectedOptionDetails(null);
                    } else {
                        Directive.unselectAllRows($scope);
                        Directive.selectRow($scope.dataTable.row(currentRowElement), $scope);
                    }
                });
            });
        }

        private static unselectAllRows($scope: IScope) {
            $scope.dataTable.$("tr.selected").removeClass("selected");
        }

        private static selectRow(row: DataTables.RowMethods, $scope: IScope) {
            if (row) {
                var node = row.node();
                var data: any = row.data();

                $(node).addClass("selected");
                if (data) {
                    $scope.updateSelectedOptionDetails(data.option.underlying.symbol);
                }
            }
        }
    }
    directives.directive("saturn.optionView.directive", ["$timeout", "hotkeys",
        ($timeout: ng.ITimeoutService, hotkeys: ng.hotkeys.HotkeysProvider) => new Saturn.OptionView.Directive($timeout, hotkeys)]);
}