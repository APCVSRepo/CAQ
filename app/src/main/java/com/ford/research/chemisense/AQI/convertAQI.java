package com.ford.research.chemisense.AQI;

//gets AQI from raw sensor values from PM2.5 sensor and CO sensor.
//Overall AQI can be obtained by taking the higher of the two.

public class convertAQI {
	
	//Used to convert PM2.5 measurements to AQI. rawValue is the raw sensor reading
	//and T is the most recent temperature value. The function outputs integer AQI.
    public static int convertPM(double ppmValue, double T){
        int pmAQI=0;

        double mC = ppmValue;

        double cHigh=0; double cLow = 0; double iHigh = 0; double iLow =0;
        if (mC>=0 && mC<12.0){
            cHigh = 12.0; cLow=0.0; iHigh=50.0; iLow = 0.0;
        } else if(mC>=12.0 && mC<35.4){
            cHigh = 35.4; cLow=12.1; iHigh=100.0; iLow = 51.0;
        } else if(mC>=35.4 && mC<55.4){
            cHigh = 55.4; cLow=35.5; iHigh=150.0; iLow = 101.0;
        } else if(mC>=55.4 && mC<150.4){
            cHigh = 150.4; cLow=55.5; iHigh=200.0; iLow = 151.0;
        } else if(mC>=150.4 && mC<250.4){
            cHigh = 250.4; cLow=150.5; iHigh=300.0; iLow = 201.0;
        } else if(mC>=250.4 && mC<350.4){
            cHigh = 350.4; cLow=250.5; iHigh=400.0; iLow = 301.0;
        } else if(mC>=350.4 && mC<500.4){
            cHigh = 500.4; cLow=350.5; iHigh=500.0; iLow = 401.0;
        } else if(mC<0){
            pmAQI=0;
            return pmAQI;
        } else{
            pmAQI=500;
            return pmAQI;
        }
        pmAQI = (int) ( ((iHigh-iLow)/(cHigh-cLow)) *(mC-cLow) +iLow);
        return pmAQI;
    }

    //Used to convert CO measurements to AQI. rawValue is the raw sensor reading
    //T is the most recent temperature value, and H is the most recent humidity value.
    //The function outputs integer AQI.
    public static int convertCO(double ppmValue, double T, double H){
        int coAQI=0;
        double ppm = ppmValue;
        double cHigh=0; double cLow = 0; double iHigh = 0; double iLow =0;

        if (ppm>=0 && ppm<4.4){
            cHigh = 4.4; cLow=0.0; iHigh=50.0; iLow = 0.0;
        } else if(ppm>=4.4 && ppm<9.4){
            cHigh = 9.4; cLow=4.5; iHigh=100.0; iLow = 51.0;
        } else if(ppm>=9.4 && ppm<12.4){
            cHigh = 12.4; cLow=9.5; iHigh=150.0; iLow = 101.0;
        } else if(ppm>=12.4 && ppm<15.4){
            cHigh = 15.4; cLow=12.5; iHigh=200.0; iLow = 151.0;
        } else if(ppm>=15.4 && ppm<30.4){
            cHigh = 30.4; cLow=15.5; iHigh=300.0; iLow = 201.0;
        } else if(ppm>=30.4 && ppm<40.4){
            cHigh = 40.4; cLow=30.5; iHigh=400.0; iLow = 301.0;
        } else if(ppm>=40.4 && ppm<50.4){
            cHigh = 50.4; cLow=40.5; iHigh=500.0; iLow = 401.0;
        } else if(ppm<0){
            coAQI=0;
            return coAQI;
        } else{
            coAQI=500;
            return coAQI;
        }
        coAQI=(int) (((iHigh-iLow)/(cHigh-cLow))*(ppm-cLow) +iLow);
        return coAQI;
    }


//	public static int convertPM(double rawValue, double T){
//		int pmAQI=0;
//		double base = 1.3314*T+161.3;
//		double s = 3.588;
//		double mC = s*(rawValue-base);
//
//		double cHigh=0; double cLow = 0; double iHigh = 0; double iLow =0;
//		if (mC>=0 && mC<12.0){
//			cHigh = 12.0; cLow=0.0; iHigh=50.0; iLow = 0.0;
//		} else if(mC>=12.0 && mC<35.4){
//			cHigh = 35.4; cLow=12.1; iHigh=100.0; iLow = 51.0;
//		} else if(mC>=35.4 && mC<55.4){
//			cHigh = 55.4; cLow=35.5; iHigh=150.0; iLow = 101.0;
//		} else if(mC>=55.4 && mC<150.4){
//			cHigh = 150.4; cLow=55.5; iHigh=200.0; iLow = 151.0;
//		} else if(mC>=150.4 && mC<250.4){
//			cHigh = 250.4; cLow=150.5; iHigh=300.0; iLow = 201.0;
//		} else if(mC>=250.4 && mC<350.4){
//			cHigh = 350.4; cLow=250.5; iHigh=400.0; iLow = 301.0;
//		} else if(mC>=350.4 && mC<500.4){
//			cHigh = 500.4; cLow=350.5; iHigh=500.0; iLow = 401.0;
//		} else if(mC<0){
//			pmAQI=0;
//			return pmAQI;
//		} else{
//			pmAQI=500;
//			return pmAQI;
//		}
//		pmAQI = (int) (((iHigh-iLow)/(cHigh-cLow))*(mC-cLow) +iLow);
//		return pmAQI;
//	}
//
//	//Used to convert CO measurements to AQI. rawValue is the raw sensor reading
//	//T is the most recent temperature value, and H is the most recent humidity value.
//	//The function outputs integer AQI.
//	public static int convertCO(double rawValue, double T, double H){
//		int coAQI=0;
//		double base = 1232 - 8.973*T + 0.1829*(Math.pow(T,new_weather_layout)) - 0.1259*H;
//		double ppm = (rawValue-base)/30.003;
//		double cHigh=0; double cLow = 0; double iHigh = 0; double iLow =0;
//
//		if (ppm>=0 && ppm<4.4){
//			cHigh = 4.4; cLow=0.0; iHigh=50.0; iLow = 0.0;
//		} else if(ppm>=4.4 && ppm<9.4){
//			cHigh = 9.4; cLow=4.5; iHigh=100.0; iLow = 51.0;
//		} else if(ppm>=9.4 && ppm<12.4){
//			cHigh = 12.4; cLow=9.5; iHigh=150.0; iLow = 101.0;
//		} else if(ppm>=12.4 && ppm<15.4){
//			cHigh = 15.4; cLow=12.5; iHigh=200.0; iLow = 151.0;
//		} else if(ppm>=15.4 && ppm<30.4){
//			cHigh = 30.4; cLow=15.5; iHigh=300.0; iLow = 201.0;
//		} else if(ppm>=30.4 && ppm<40.4){
//			cHigh = 40.4; cLow=30.5; iHigh=400.0; iLow = 301.0;
//		} else if(ppm>=40.4 && ppm<50.4){
//			cHigh = 50.4; cLow=40.5; iHigh=500.0; iLow = 401.0;
//		} else if(ppm<0){
//			coAQI=0;
//			return coAQI;
//		} else{
//			coAQI=500;
//			return coAQI;
//		}
//		coAQI=(int) (((iHigh-iLow)/(cHigh-cLow))*(ppm-cLow) +iLow);
//		return coAQI;
//	}
	//returns a html color hexcode from AQI.
	public static String getColor(int AQI){
		String hexColor="";
		if (AQI>=0 && AQI<50){
			hexColor="#00E400";
		} else if (AQI>=50 && AQI<100){
			hexColor="#FFFF00";
		} else if (AQI>=100 && AQI<150){
			hexColor="#FF7E00";
		} else if (AQI>=150 && AQI<200){
			hexColor="#FF0000";
		} else if (AQI>=200 && AQI<300){
			hexColor="#99004C";
		} else if (AQI>=300 && AQI<400){
			hexColor="#7E0023";
		} else{
			hexColor="#7E0023";
		}
		return hexColor;
	}	
}
