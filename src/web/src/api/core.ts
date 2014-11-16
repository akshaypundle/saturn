module Saturn.Api {
    export interface Instrument {
        symbol: string;
        last: number;
        bid: number;
        ask: number;
    }

    export class OptionType {
        public static CALL = "CALL";
        public static PUT = "PUT";
    }

    export interface Option extends Instrument {
        underlying: string;
        expiry: string;
        type: OptionType;
    }
}