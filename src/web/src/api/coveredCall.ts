module Saturn.Api {
    export class CoveredCallType {
        public static ITM = "ITM"; // in the money
        public static OTM = "OTM"; // out of the money
    }

    export interface CoveredCall {
        option: Option;
        type: CoveredCallType;
    }

    export interface ITMCoveredCall extends CoveredCall {
        roi: number;
        downsideProtection: number;
        breakEven: number;
    }

    export interface OTMCoveredCall extends CoveredCall {
        expectedRoi: number;
        upsideRequired: number;
    }
}