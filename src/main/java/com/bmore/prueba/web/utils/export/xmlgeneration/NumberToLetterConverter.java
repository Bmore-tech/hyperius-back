package com.bmore.prueba.web.utils.export.xmlgeneration;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public abstract class NumberToLetterConverter {

	private static final String[] UNIDADES = { "", "UN ", "DOS ", "TRES ",
			"CUATRO ", "CINCO ", "SEIS ", "SIETE ", "OCHO ", "NUEVE ", "DIEZ ",
			"ONCE ", "DOCE ", "TRECE ", "CATORCE ", "QUINCE ", "DIECISEIS",
			"DIECISIETE", "DIECIOCHO", "DIECINUEVE", "VEINTE" };

	private static final String[] DECENAS = { "VEINTI", "TREINTA ",
			"CUARENTA ", "CINCUENTA ", "SESENTA ", "SETENTA ", "OCHENTA ",
			"NOVENTA ", "CIEN " };

	private static final String[] CENTENAS = { "CIENTO ", "DOSCIENTOS ",
			"TRESCIENTOS ", "CUATROCIENTOS ", "QUINIENTOS ", "SEISCIENTOS ",
			"SETECIENTOS ", "OCHOCIENTOS ", "NOVECIENTOS " };

	private static final String[] UNITS = { "", "ONE", "TWO", "THREE", "FOUR",
			"FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "ELEVEN", "TWELVE",
			"THIRTEEN", "FOURTEEN", "FIFTEEN", "SIXTEEN", "SEVENTEEN",
			"EIGHTEEN", "NINETEEN", "TWENTY" };

	private static final String[] TENS = { "TWENTY", "THIRTY", "FORTY",
			"FIFTY", "SIXTY", "SEVENTY", " EIGHTY", " NINETY" };

	private static final String[] CENTS = { "ONE HUNDRED AND",
			"TWO HUNDRED AND", "THREE HUNDRED AND", "FOUR HUNDRED AND",
			"FIVE HUNDRED AND", "SIX HUNDRED AND", "SEVEN HUNDRED AND", "EIGHT HUNDRED AND",
			"NINE HUNDRED AND" };

	/**
	 * Convierte a letras un numero de la forma $123,456.32
	 * 
	 * @param number
	 *            Numero en representacion texto
	 * @throws NumberFormatException
	 *             Si valor del numero no es valido (fuera de rango o )
	 * @return Numero en letras
	 */
	public static String convertNumberToLetter(String number)
			throws NumberFormatException {
		// return convertNumberToLetter(Double.parseDouble(number));
		String[] numberSend = number.replace(".", "#").split("#");
		// return convertNumberToLetterEntero((numberSend[0])) + " CON " +
		// convertNumberToLetterCentavos(numberSend[1]);
		return "" + convertNumberToLetterEntero((numberSend[0])) + " "
				+ numberSend[1] + "/100";

	}

	public static String convertNumberToLetterInglich(String number)
			throws NumberFormatException {
		// return convertNumberToLetter(Double.parseDouble(number));
		String[] numberSend = number.replace(".", "#").split("#");
		// return convertNumberToLetterEntero((numberSend[0])) + " CON " +
		// convertNumberToLetterCentavos(numberSend[1]);
		return "" + convertNumberToLetterEnteroInglich((numberSend[0]))
				+ " " + numberSend[1] + "/100";

	}

	/**
	 * Convierte un numero en representacion numerica a uno en representacion de
	 * texto. El numero es valido si esta entre 0 y 999'999.999
	 * 
	 * @param number
	 *            Numero a convertir
	 * @return Numero convertido a texto
	 * @throws NumberFormatException
	 *             Si el numero esta fuera del rango
	 */
	public static String convertNumberToLetter(double doubleNumber)
			throws NumberFormatException {

		StringBuilder converted = new StringBuilder();

		String patternThreeDecimalPoints = "#.###";

		DecimalFormat format = new DecimalFormat(patternThreeDecimalPoints);
		format.setRoundingMode(RoundingMode.DOWN);

		// formateamos el numero, para ajustarlo a el formato de tres puntos
		// decimales
		String formatedDouble = format.format(doubleNumber);
		doubleNumber = Double.parseDouble(formatedDouble);

		// Validamos que sea un numero legal
		if (doubleNumber > 999999999)
			throw new NumberFormatException(
					"El numero es mayor de 999'999.999, "
							+ "no es posible convertirlo");

		if (doubleNumber < 0)
			throw new NumberFormatException("El numero debe ser positivo");

		String splitNumber[] = String.valueOf(doubleNumber).replace('.', '#')
				.split("#");

		// Descompone el trio de millones
		int millon = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0],
				8))
				+ String.valueOf(getDigitAt(splitNumber[0], 7))
				+ String.valueOf(getDigitAt(splitNumber[0], 6)));
		if (millon == 1)
			converted.append("UN MILLON ");
		else if (millon > 1)
			converted.append(convertNumber(String.valueOf(millon))
					+ "MILLONES ");

		// Descompone el trio de miles
		int miles = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0],
				5))
				+ String.valueOf(getDigitAt(splitNumber[0], 4))
				+ String.valueOf(getDigitAt(splitNumber[0], 3)));
		if (miles == 1)
			converted.append("MIL ");
		else if (miles > 1)
			converted.append(convertNumber(String.valueOf(miles)) + "MIL ");

		// Descompone el ultimo trio de unidades
		int cientos = Integer.parseInt(String.valueOf(getDigitAt(
				splitNumber[0], 2))
				+ String.valueOf(getDigitAt(splitNumber[0], 1))
				+ String.valueOf(getDigitAt(splitNumber[0], 0)));
		if (cientos == 1)
			converted.append("UN");

		if (millon + miles + cientos == 0)
			converted.append("CERO");
		if (cientos > 1)
			converted.append(convertNumber(String.valueOf(cientos)));

		//converted.append("PESOS");

		return converted.toString();
	}

	public static String convertNumberToLetterEntero(String doubleNumber)
			throws NumberFormatException {

		StringBuilder converted = new StringBuilder();

		if (doubleNumber.length() > 9)
			throw new NumberFormatException(
					"El numero es mayor de 999'999.999, "
							+ "no es posible convertirlo");

		String splitNumber[] = String.valueOf(doubleNumber).replace('.', '#')
				.split("#");

		// Descompone el trio de millones
		int millon = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0],
				8))
				+ String.valueOf(getDigitAt(splitNumber[0], 7))
				+ String.valueOf(getDigitAt(splitNumber[0], 6)));
		if (millon == 1)
			converted.append("UN MILLON ");
		else if (millon > 1)
			converted.append(convertNumber(String.valueOf(millon))
					+ "MILLONES ");

		// Descompone el trio de miles
		int miles = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0],
				5))
				+ String.valueOf(getDigitAt(splitNumber[0], 4))
				+ String.valueOf(getDigitAt(splitNumber[0], 3)));
		if (miles == 1)
			converted.append("MIL ");
		else if (miles > 1)
			converted.append(convertNumber(String.valueOf(miles)) + "MIL ");

		// Descompone el ultimo trio de unidades
		int cientos = Integer.parseInt(String.valueOf(getDigitAt(
				splitNumber[0], 2))
				+ String.valueOf(getDigitAt(splitNumber[0], 1))
				+ String.valueOf(getDigitAt(splitNumber[0], 0)));
		if (cientos == 1)
			converted.append("UN");

		if (millon + miles + cientos == 0)
			converted.append("CERO");
		if (cientos > 1)
			converted.append(convertNumber(String.valueOf(cientos)));

		//converted.append("PESOS");
		return converted.toString();
	}

	public static String convertNumberToLetterEnteroInglich(String doubleNumber)
			throws NumberFormatException {

		StringBuilder converted = new StringBuilder();

		if (doubleNumber.length() > 9)
			throw new NumberFormatException(
					"El numero es mayor de 999'999.999, "
							+ "no es posible convertirlo");

		String splitNumber[] = String.valueOf(doubleNumber).replace('.', '#')
				.split("#");

		// Descompone el trio de millones
		int millon = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0],
				8))
				+ String.valueOf(getDigitAt(splitNumber[0], 7))
				+ String.valueOf(getDigitAt(splitNumber[0], 6)));
		if (millon == 1)
			converted.append("ONE MILLON ");
		else if (millon > 1)
			converted.append(convertNumberInglich(String.valueOf(millon))
					+ " MILLION ");

		// Descompone el trio de miles
		int miles = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0],
				5))
				+ String.valueOf(getDigitAt(splitNumber[0], 4))
				+ String.valueOf(getDigitAt(splitNumber[0], 3)));
		if (miles == 1)
			converted.append(" THOUSAND ");
		else if (miles > 1)
			converted.append(convertNumberInglich(String.valueOf(miles))
					+ " THOUSAND ");

		// Descompone el ultimo trio de unidades
		int cientos = Integer.parseInt(String.valueOf(getDigitAt(
				splitNumber[0], 2))
				+ String.valueOf(getDigitAt(splitNumber[0], 1))
				+ String.valueOf(getDigitAt(splitNumber[0], 0)));
		if (cientos == 1)
			converted.append("ONE");

		if (millon + miles + cientos == 0)
			converted.append("ZERO");
		if (cientos > 1)
			converted.append(convertNumberInglich(String.valueOf(cientos)));

		//converted.append(" PESOS");
		return converted.toString();
	}

	public static String convertNumberToLetterCentavos(String doubleNumber)
			throws NumberFormatException {

		StringBuilder converted = new StringBuilder();

		if (doubleNumber.length() > 3)
			throw new NumberFormatException("El numero es mayor de .999, "
					+ "no es posible convertirlo");

		String splitNumber[] = String.valueOf(doubleNumber).replace('.', '#')
				.split("#");

		// Descompone el trio de millones
		int millon = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0],
				8))
				+ String.valueOf(getDigitAt(splitNumber[0], 7))
				+ String.valueOf(getDigitAt(splitNumber[0], 6)));
		if (millon == 1)
			converted.append("UN MILLON ");
		else if (millon > 1)
			converted.append(convertNumber(String.valueOf(millon))
					+ "MILLONES ");

		// Descompone el trio de miles
		int miles = Integer.parseInt(String.valueOf(getDigitAt(splitNumber[0],
				5))
				+ String.valueOf(getDigitAt(splitNumber[0], 4))
				+ String.valueOf(getDigitAt(splitNumber[0], 3)));
		if (miles == 1)
			converted.append("MIL ");
		else if (miles > 1)
			converted.append(convertNumber(String.valueOf(miles)) + "MIL ");

		// Descompone el ultimo trio de unidades
		int cientos = Integer.parseInt(String.valueOf(getDigitAt(
				splitNumber[0], 2))
				+ String.valueOf(getDigitAt(splitNumber[0], 1))
				+ String.valueOf(getDigitAt(splitNumber[0], 0)));
		if (cientos == 1)
			converted.append("UN CENTAVO");

		if (millon + miles + cientos == 0)
			converted.append("CERO");
		if (cientos > 1) {
			converted.append(convertNumber(String.valueOf(cientos)));
			converted.append("CENTAVOS");
		}
		return converted.toString();
	}

	/**
	 * Convierte los trios de numeros que componen las unidades, las decenas y
	 * las centenas del numero.
	 * 
	 * @param number
	 *            Numero a convetir en digitos
	 * @return Numero convertido en letras
	 */
	private static String convertNumber(String number) {

		if (number.length() > 3)
			throw new NumberFormatException(
					"La longitud maxima debe ser 3 digitos");

		// Caso especial con el 100
		if (number.equals("100")) {
			return "CIEN";
		}

		StringBuilder output = new StringBuilder();
		if (getDigitAt(number, 2) != 0)
			output.append(CENTENAS[getDigitAt(number, 2) - 1]);

		int k = Integer.parseInt(String.valueOf(getDigitAt(number, 1))
				+ String.valueOf(getDigitAt(number, 0)));

		if (k <= 20)
			output.append(UNIDADES[k]);
		else if (k > 30 && getDigitAt(number, 0) != 0)
			output.append(DECENAS[getDigitAt(number, 1) - 2] + "Y "
					+ UNIDADES[getDigitAt(number, 0)]);
		else
			output.append(DECENAS[getDigitAt(number, 1) - 2]
					+ UNIDADES[getDigitAt(number, 0)]);

		return output.toString();
	}

	private static String convertNumberInglich(String number) {

		if (number.length() > 3)
			throw new NumberFormatException(
					"La longitud maxima debe ser 3 digitos");

		// Caso especial con el 100
		if (number.equals("100")) {
			return "ONE HUNDRED";
		}

		StringBuilder output = new StringBuilder();
		if (getDigitAt(number, 2) != 0)
			output.append(CENTS[getDigitAt(number, 2) - 1] + " ");

		int k = Integer.parseInt(String.valueOf(getDigitAt(number, 1))
				+ String.valueOf(getDigitAt(number, 0)));

		if (k <= 20)
			output.append(UNITS[k]);
		else if (k > 30 && getDigitAt(number, 0) != 0)
			output.append(TENS[getDigitAt(number, 1) - 2] + "-"
					+ UNITS[getDigitAt(number, 0)]);
		else
			output.append(TENS[getDigitAt(number, 1) - 2] + "-"
					+ UNITS[getDigitAt(number, 0)]);

		return output.toString();
	}

	/**
	 * Retorna el digito numerico en la posicion indicada de derecha a izquierda
	 * 
	 * @param origin
	 *            Cadena en la cual se busca el digito
	 * @param position
	 *            Posicion de derecha a izquierda a retornar
	 * @return Digito ubicado en la posicion indicada
	 */
	private static int getDigitAt(String origin, int position) {
		if (origin.length() > position && position >= 0)
			return origin.charAt(origin.length() - position - 1) - 48;
		return 0;
	}

}