module Saturn.Api {
    export interface Instrument {
        ask: number;
        bid: number;
        symbol: string;
    }

    export class OptionType {
        public static CALL = "CALL";
        public static PUT = "PUT";
    }

    export interface Option extends Instrument {
        expiry: string;
        strike: number;
        type: OptionType;
        underlying: Instrument;
    }
}