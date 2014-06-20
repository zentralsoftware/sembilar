package com.central.varth.resp.parser.impl;

import com.central.varth.resp.MovedInfo;
import com.central.varth.resp.ProtocolConstant;
import com.central.varth.resp.parser.AbstractRedirectionParser;

public class RedisMovedRedirectionParser extends AbstractRedirectionParser<MovedInfo> {

	/**
	 * raw format:
	 * -MOVED slot address:port
	 */
	@Override
	public MovedInfo parse(String raw) {
		String[] cols = raw.split(ProtocolConstant.SPACE);
		MovedInfo info = new MovedInfo(raw);
		info.setSlot(Integer.parseInt(cols[1]));	
		info.setAddress(getAddress(cols[2]));
		return info;
	}
	
}
