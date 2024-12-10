package com.example.apitestmanager.atm.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class JsonUtils {

//    public JSONObject processJSONObject(JSONObject jsonObject, String condition) {
//        if (!condition.equals("all") && !condition.equals("mandatory") && !condition.equals("non-mandatory")) {
//            throw new IllegalArgumentException("Invalid condition. Allowed values are 'all', 'mandatory', 'non-mandatory'.");
//        }
//        jsonObject = processObject(jsonObject, condition);
//
//        return jsonObject;
//    }

    public JSONObject processJSONObject(Object jsonElement, String condition) {
        if (jsonElement instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) jsonElement;
            List<String> keysToRemove = new ArrayList<>();
            AtomicBoolean hasMandatoryNestedObject = new AtomicBoolean(false); // Checking if any mandatory fields available inside non-mandatory MAP.
            jsonObject.keySet().forEach(key -> {
                Object value = jsonObject.get(key);
                if (value instanceof JSONObject) {
                    JSONObject nestedObject = (JSONObject) value;
                    boolean nestedObjectPresent = processJSONObject(nestedObject, condition) != null;
                    if (nestedObjectPresent) {
                        hasMandatoryNestedObject.set(true);
                    }
                } else if (value instanceof JSONArray) {
                    JSONArray nestedArray = (JSONArray) value;
                    if (nestedArray.get(0) instanceof JSONObject) {
                        boolean nestedObjectPresent = processJSONObject(nestedArray, condition) != null;
                        if (nestedObjectPresent) {
                            hasMandatoryNestedObject.set(true);
                        }
                    } else if (condition.equals("mandatory") && !key.contains("$")) {
                        keysToRemove.add(key);
                    } else if (condition.equals("non-mandatory") && key.contains("$")) {
                        keysToRemove.add(key);
                    }
                } else if (condition.equals("mandatory") && !key.contains("$")) {
                    keysToRemove.add(key);
                } else if (condition.equals("non-mandatory") && key.contains("$")) {
                    keysToRemove.add(key);
                }
            });
            keysToRemove.forEach(jsonObject::remove);
            return hasMandatoryNestedObject.get() || jsonObject.length() > 0 ? jsonObject : null;
        } else if (jsonElement instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) jsonElement;
            for (int i = 0; i < jsonArray.length(); i++) {
                processJSONObject(jsonArray.get(i), condition);
            }
        }
        return null;
    }

    public List<String> createPayloads(JSONObject jsonObject) throws JsonProcessingException {
        List<String> payloads = new ArrayList<>();
        List<String> conditions = new ArrayList<>(Arrays.asList("all", "mandatory", "non-mandatory"));
        for (String condition : conditions) {
            JSONObject temp = new JSONObject(jsonObject.toString());
            switch (condition) {
                case "all":
                    temp = processJSONObject(temp, "all");
                    break;
                case "mandatory":
                    temp = processJSONObject(temp, "mandatory");
                    break;
                case "non-mandatory":
                    temp = processJSONObject(temp, "non-mandatory");
                    break;
                default:
                    log.error("Error in creating body...");
                    break;
            }
            temp = (JSONObject) processJSONValues(temp);
            String jsonString = AppUtils.objectToJsonString(temp.toString());
            payloads.add(jsonString);
        }
        return payloads;
    }

    public Object processJSONValues(Object jsonElement) {
        if (jsonElement instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) jsonElement;
            jsonObject.keySet().forEach(key -> {
                if (jsonObject.get(key) instanceof String) {
                    jsonObject.put(key, UUID.randomUUID().toString());
                } else if (jsonObject.get(key) instanceof Integer) {
                    jsonObject.put(key, new Random().nextInt());
                } else if (jsonObject.get(key) instanceof BigInteger) {
                    jsonObject.put(key, new Random().nextInt());
                } else if (jsonObject.get(key) instanceof Double) {
                    jsonObject.put(key, new Random().nextDouble());
                } else if (jsonObject.get(key) instanceof BigDecimal) {
                    jsonObject.put(key, new Random().nextDouble());
                } else if (jsonObject.get(key) instanceof Boolean) {
                    jsonObject.put(key, new Random().nextBoolean());
                } else if (jsonObject.get(key) instanceof JSONObject) {
                    processJSONValues(jsonObject.get(key));
                } else if (jsonObject.get(key) instanceof JSONArray) {
                    processJSONValues(jsonObject.get(key));
                }
            });
        } else if (jsonElement instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) jsonElement;
            if (jsonArray.get(0) instanceof JSONObject) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    processJSONValues(jsonArray.get(i));
                }
            } else {
                if (jsonArray.get(0) instanceof String) {
                    jsonArray.clear();
                    for (int i = 0; i < 3; i++) {
                        jsonArray.put(UUID.randomUUID().toString());
                    }
                } else if (jsonArray.get(0) instanceof Integer) {
                    jsonArray.clear();
                    for (int i = 0; i < 3; i++) {
                        jsonArray.put(new Random().nextInt());
                    }
                } else if (jsonArray.get(0) instanceof Double) {
                    jsonArray.clear();
                    for (int i = 0; i < 3; i++) {
                        jsonArray.put(new Random().nextDouble());
                    }
                } else if (jsonArray.get(0) instanceof Boolean) {
                    jsonArray.clear();
                    for (int i = 0; i < 3; i++) {
                        jsonArray.put(new Random().nextBoolean());
                    }
                }
            }
        }

        return jsonElement;
    }

}
