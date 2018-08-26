package com.roadeed.sh.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hp on 12/15/2017.
 */

public class JsonResult {
  private String result;
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}