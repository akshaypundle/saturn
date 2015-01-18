module Saturn.Util {
    export function renderPercent(data: any) {
        if (data !== null && !isNaN(data)) {
            return (data * 100).toFixed(2);
        } else {
            return "--";
        };
    }
}