/*
 * Copyright (c) 2015-2024, David A. Bauer. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.actor4j.json;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.actor4j.core.json.ActorJsonArray;
import io.actor4j.core.json.ActorJsonObject;

public class DefaultActorJsonArray implements ActorJsonArray {
	protected JSONArray jsonArray;
	
	public DefaultActorJsonArray() {
		super();
		
		jsonArray = new JSONArray();
	}
	
	public DefaultActorJsonArray(List<?> list) {
		super();
		
		try {
			jsonArray = new JSONArray(list);
		} catch(JSONException e) {
			jsonArray = new JSONArray();
			e.printStackTrace();
		}
	}
	
	public DefaultActorJsonArray(String json) {
		super();
		
		try {
			jsonArray = new JSONArray(json);
		} catch(JSONException e) {
			jsonArray = new JSONArray();
			e.printStackTrace();
		}
	}
	
	public DefaultActorJsonArray(JSONArray arr) {
		super();
		
		if (arr!=null)
			jsonArray = arr;
		else
			jsonArray = new JSONArray();
	}

	@Override
	public Object getValue(int pos) {
		Object result = null;
		try {
			result = jsonArray.get(pos);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public String getString(int pos) {
		String result = null;
		try {
			result = jsonArray.getString(pos);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public Integer getInteger(int pos) {
		Integer result = null;
		try {
			result = jsonArray.getInt(pos);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public Long getLong(int pos) {
		Long result = null;
		try {
			result = jsonArray.getLong(pos);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public Double getDouble(int pos) {
		Double result = null;
		try {
			result = jsonArray.getDouble(pos);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public Boolean getBoolean(int pos) {
		Boolean result = null;
		try {
			result = jsonArray.getBoolean(pos);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public ActorJsonObject getJsonObject(int pos) {
		JSONObject result = null;
		try {
			result = jsonArray.getJSONObject(pos);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
		return result!=null ? new DefaultActorJsonObject(result) : null;
	}

	@Override
	public ActorJsonArray getJsonArray(int pos) {
		JSONArray result = null;
		try {
			result = jsonArray.getJSONArray(pos);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
		return result!=null ? new DefaultActorJsonArray(result) : null;
	}

	@Override
	public ActorJsonArray add(Object value) {
		try {
			jsonArray.put(value);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
		return this;
	}

	@Override
	public ActorJsonArray add(int pos, Object value) {
		try {
			jsonArray.put(pos, value);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
		return this;
	}

	@Override
	public ActorJsonArray addAll(ActorJsonArray array) {
		if (array!=null && array instanceof DefaultActorJsonArray array_)
			for (Object value : array_.jsonArray.toList()) {
				try {
					jsonArray.put(value);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		return this;
	}

	@Override
	public ActorJsonArray set(int pos, Object value) {
		try {
			jsonArray.put(pos, value);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
		return this;
	}

	@Override
	public boolean contains(Object value) {
		boolean result = false;
		
		if (value!=null)
			result = jsonArray.toList().stream().anyMatch((v) -> v!=null ? v.equals(value) : false);

		return result;
	}

	@Override
	public Object remove(int pos) {
		return jsonArray.remove(pos);
	}

	@Override
	public int size() {
		return jsonArray.length();
	}

	@Override
	public boolean isEmpty() {
		return jsonArray.isEmpty();
	}

	@Override
	public List<Object> getList() {
		return jsonArray.toList();
	}

	@Override
	public ActorJsonArray clear() {
		jsonArray.clear();
		
		return this;
	}

	@Override
	public String encode() {
		return jsonArray.toString();
	}

	@Override
	public String encodePrettily() {
		String result = null;
		try {
			result = jsonArray.toString(2);
		} catch(JSONException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}