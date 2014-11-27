declare module jquery.dataTables {
    interface IDTOptionsBuilder {
        fromSource(data: string): IDTOptionsBuilder;
        fromFnPromise(promiseFn: () => ng.IPromise<any>): IDTOptionsBuilder;
        withPaginationType(paginationType: string): IDTOptionsBuilder;
        withOption(key: any, value: any): IDTOptionsBuilder;
        withBootstrap(): IDTOptionsBuilder;
        withTableTools(swfPath: string): IDTOptionsBuilder;
        withTableToolsButtons(param: any): IDTOptionsBuilder;
    }

    interface IDTColumnBuilder {
        newColumn(name: string): IDTColumnBuilder;
        withTitle(title: string): IDTColumnBuilder;
    }
}