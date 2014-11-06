module Saturn.Api {
    export interface Instrument {
        symbol: string;
        last: number;
        bid: number;
        ask: number;
    }

    export interface Option extends Instrument {
        underlying: string;
        expiry: string;
    }
}