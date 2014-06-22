/**
 * 
 * Copyright ${year} Central Software

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package com.central.sembilar.resp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.central.sembilar.resp.type.BulkString;
import com.central.sembilar.resp.type.RespArray;
import com.central.sembilar.resp.type.RespInteger;
import com.central.sembilar.resp.type.RespType;
import com.central.sembilar.resp.type.SimpleString;
import com.central.sembilar.resp.type.TypeConstant;

public class RespCommandDeserializer {

	private BufferedReader reader;
	
	public RespCommandDeserializer(InputStream is) throws IOException, RespException
	{
		InputStreamReader isr = new InputStreamReader(is);
		reader = new BufferedReader(isr);
	}
	
	public RespCommandDeserializer(InputStream is, int bufferSize) throws IOException, RespException
	{
		InputStreamReader isr = new InputStreamReader(is);
		reader = new BufferedReader(isr, bufferSize);
	}	
	
	public <T extends RespType> T deserialize(Class<T> type) throws IOException, RespException
	{
		String line = reader.readLine();
		char typeMarker = line.charAt(0);
		switch (typeMarker) {
			case TypeConstant.SIMPLE_STRING_TYPE:
				return type.cast(parseResponseToSimpleString(line));
			case TypeConstant.INTEGER_TYPE:
				return type.cast(parseResponseToRespInteger(line));
			case TypeConstant.BULK_STRING_TYPE:
				return type.cast(parseResponseToBulkString(line, reader));
			case TypeConstant.ARRAY_TYPE:
				return type.cast(parseResponseToRespArray(line, reader));
			default:
				throw processException(line);
		}
	}	
	
	private RespException processException(String line)
	{
		if (line.startsWith(TypeConstant.ERROR_TYPE + ProtocolConstant.RESPONSE_MOVED))
		{
			return new MovedException(line);
		} else if (line.startsWith(TypeConstant.ERROR_TYPE + ProtocolConstant.RESPONSE_ASK))
		{
			return new AskException(line);
		} else
		{
			return new RespException(line);
		}
	}
	
	protected BulkString parseResponseToBulkString(String line, BufferedReader reader) throws IOException
	{
		BulkString bulkString = new BulkString();
		int len = getLengthInteger(line);
		if (len == -1)
		{
			return createNullBulkString();
		}
		char[] chars = new char[len];
		for (int i=0;i<len;i++)
		{
			chars[i] = (char) reader.read();
		}
		reader.skip(2);	// skip \r\n
		String data = new String(chars);
		bulkString.setSize(len);
		bulkString.setString(data);
		return bulkString;		
	}
	
	private BulkString createNullBulkString()
	{
		BulkString bulkString = new BulkString();
		bulkString.setSize(-1);
		bulkString.setString(null);
		return bulkString;
	}
	
	protected SimpleString parseResponseToSimpleString(String line) 
	{
		String string = line.substring(1);
		SimpleString simpleString = new SimpleString();
		simpleString.setString(string);
		return simpleString;	
	}	
	
	protected RespInteger parseResponseToRespInteger(String line) 
	{
		int i = getLengthInteger(line);
		RespInteger respInteger = new RespInteger();
		respInteger.setInteger(i);
		return respInteger;	
	}
	
	protected RespArray parseResponseToRespArray(String line, BufferedReader reader) throws IOException, RespException
	{
		int len = getLengthInteger(line);
		if (len == -1)
		{
			return createNullArray();
		}
		RespArray arr = new RespArray();
		arr.setSize(len);
		for (int i=0;i<len;i++)
		{
			RespType element = deserialize(RespType.class);
			arr.getElement().add(element);
		}
		return arr;
	}
	
	private RespArray createNullArray() {
		RespArray array = new RespArray();
		array.setSize(-1);
		array.setElement(null);
		return array;
	}

	protected int getLengthInteger(String line)
	{
		String prefixedLen = line.substring(1);
		int len = Integer.parseInt(prefixedLen);
		return len;
	}
}
