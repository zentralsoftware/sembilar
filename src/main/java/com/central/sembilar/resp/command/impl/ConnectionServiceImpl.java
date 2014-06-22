/**
 * 
 * Copyright 2014 Central Software

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

package com.central.sembilar.resp.command.impl;

import java.io.IOException;

import com.central.sembilar.resp.ProtocolConstant;
import com.central.sembilar.resp.RespException;
import com.central.sembilar.resp.RespSerializer;
import com.central.sembilar.resp.command.ConnectionService;
import com.central.sembilar.resp.connection.ConnectionManager;
import com.central.sembilar.resp.type.SimpleString;

public class ConnectionServiceImpl implements ConnectionService
{

	private ConnectionManager connectionManager;
	
	@Override
	public String auth(String password) throws IOException, RespException {
		RespSerializer serializer = new RespSerializer();
		String cmd = serializer.serialize(ProtocolConstant.COMMAND_AUTH, password);
		SimpleString resp = connectionManager.send(cmd, SimpleString.class);
		return resp.getString();
	}
	
	@Override
	public String ping() throws IOException, RespException {
		RespSerializer serializer = new RespSerializer();
		String cmd = serializer.serialize(ProtocolConstant.COMMAND_PING);
		SimpleString resp = connectionManager.send(cmd, SimpleString.class);
		return resp.getString();
	}

	@Override
	public String echo(String message) throws IOException, RespException {
		RespSerializer serializer = new RespSerializer();
		String cmd = serializer.serialize(ProtocolConstant.COMMAND_ECHO, message);
		SimpleString resp = connectionManager.send(cmd, SimpleString.class);
		return resp.getString();
	}

	@Override
	public void setConnectionManager(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}
}
