/*
 * Copyright (C) 2019 Université de Limoges
 *
 * This file is part of CovCopCan.
 *
 * CovCopCan is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CovCopCan is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CovCopCan. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package ngsdiaglim.enumerations;

public enum CNVTypes {
	DELETION("Deletion"),
	DUPLICATION("Duplication"),
	NORMAL("Normal"),
	UNDEFINED("Undefined");
	
	private String CNVType;  
	
	private CNVTypes(String t) {  
        this.CNVType = t ;  
    }  
	
	public String getCNVType(){
	   return this.CNVType;
	}
}
