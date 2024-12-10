import React, { useState, useEffect } from "react";
import axios from "axios";
import "./FileUpload.css";
import axios from "axios";

function FileUpload() {
  const [file, setFile] = useState(null);
  const [jsonData, setJsonData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [dataProcessing, setDataProcessing] = useState(false);
  const [expandedItem, setExpandedItem] = useState({
    passed: null,
    failed: null,
  });

  // Track expanded state for each column
  const [passedCount, setPassedCount] = useState(0); // Number of passed items
  const [failedCount, setFailedCount] = useState(0); // Number of failed items

  const handleFileChange = (event) => {
    setFile(event.target.files[0]);
  };

  const handleFileUpload = async () => {
    if (!file) return;
    setLoading(true);
    setDataProcessing(false);
    setJsonData([]);

    const formData = new FormData();
    formData.append("file", file);

    try {
      const uploadResponse = await axios.post(
        "http://localhost:8080/upload-file",
        formData,
        {
          headers: { "Content-Type": "multipart/form-data" },
        }
      );

      const { file_path } = uploadResponse.data;

      setDataProcessing(true);
      const extractResponse = await axios.post(
        "http://localhost:8080/extract-data",
        { file_path }
      );

      if (Array.isArray(extractResponse.data)) {
        setJsonData(extractResponse.data);
      } else {
        console.error("Unexpected response format:", extractResponse.data);
      }
    } catch (error) {
      console.error("Error processing the file:", error);
    } finally {
      setLoading(false);
      setDataProcessing(false);
    }
  };

  const getJsonStatusClass = (statusCode) => {
    if (statusCode >= 200 && statusCode < 300) return "jsonStatusCodeSuccess";
    if (statusCode >= 400 && statusCode < 500) return "jsonStatusCodeError";
    return "";
  };

  const removeKeys = (item) => {
    const { status_code, base_url, endpoint, method, request_body, ...rest } =
      item;
    return rest;
  };

  const toggleExpand = (index, status) => {
    setExpandedItem((prev) => ({
      ...prev,
      [status]: prev[status] === index ? null : index,
    }));
  };

  const expandAll = (status) => {
    setExpandedItem((prev) => ({
      ...prev,
      [status]: jsonData
        .filter((item) =>
          status === "passed"
            ? item.status_code >= 200 && item.status_code < 300
            : item.status_code >= 400 && item.status_code < 500
        )
        .map((_, index) => index),
    }));
  };

  const collapseAll = (status) => {
    setExpandedItem((prev) => ({
      ...prev,
      [status]: null,
    }));
  };

  // Calculate the number of passed and failed items when jsonData is updated
  useEffect(() => {
    if (jsonData.length > 0) {
      const passed = jsonData.filter(
        (item) => item.status_code >= 200 && item.status_code < 300
      ).length;
      const failed = jsonData.filter(
        (item) => item.status_code >= 400 && item.status_code < 500
      ).length;
      setPassedCount(passed);
      setFailedCount(failed);
    }
  }, [jsonData]);

  return (
    <div className="container">
      <div className="header">
        <div className="navBar">Smart API Tool</div>
        {/* <hr /> */}
        <div className="fileInputContainer">
          <input
            type="file"
            className="fileInput"
            onChange={handleFileChange}
          />
          <button
            className="button"
            onClick={handleFileUpload}
            disabled={loading}
          >
            {loading ? "Processing..." : "Upload"}
          </button>
        </div>
      </div>
      <div className="jsonDataContainer">
        <div className="columnsContainer">
          <div className="column passed">
            <h3>Passed ({passedCount})</h3>
            <div className="passedContainer">
              {jsonData
                .filter(
                  (item) => item.status_code >= 200 && item.status_code < 300
                )
                .map((item, index) => {
                  const cleanedItem = removeKeys(item);
                  const requestBody = item.request_body
                    ? JSON.parse(item.request_body)
                    : null;
                  const isExpanded = expandedItem.passed === index;

                  return (
                    <div key={index} className="jsonContainer passed">
                      <div
                        onClick={() => toggleExpand(index, "passed")}
                        className="jsonHeader"
                      >
                        <span
                          className={`toggleIcon ${
                            isExpanded ? "expanded" : "collapsed"
                          } passed`}
                        ></span>
                        <div className="jsonHeaderDetails">
                          <div className="details">
                            <strong>Base URL:</strong> {item.base_url}
                          </div>
                          <div className="details">
                            <strong>Endpoint:</strong> {item.endpoint}
                          </div>
                          <div className="details">
                            <strong>Method:</strong>{" "}
                            <span className="method">{item.method}</span>
                          </div>
                          <div
                            className={`jsonStatusCode ${getJsonStatusClass(
                              item.status_code
                            )}`}
                          >
                            Status Code: {item.status_code}
                          </div>
                        </div>
                      </div>
                      {isExpanded && (
                        <div className="jsonDetailsExpanded">
                          {requestBody && (
                            <pre className="jsonData">
                              <strong>
                                Request:
                                <br />
                              </strong>
                              <br />
                              {JSON.stringify(requestBody, null, 2)}
                            </pre>
                          )}
                          <pre className="jsonData">
                            <strong>
                              Response:
                              <br />
                            </strong>
                            <br />
                            {JSON.stringify(cleanedItem, null, 2)}
                          </pre>
                        </div>
                      )}
                    </div>
                  );
                })}
            </div>
          </div>
          <div className="column failed">
            <h3>Failed ({failedCount})</h3>
            <div className="failedContainer">
              {jsonData
                .filter(
                  (item) => item.status_code >= 400 && item.status_code < 500
                )
                .map((item, index) => {
                  const cleanedItem = removeKeys(item);
                  const requestBody = item.request_body
                    ? JSON.parse(item.request_body)
                    : null;
                  const isExpanded = expandedItem.failed === index;

                  return (
                    <div key={index} className="jsonContainer failed">
                      <div
                        onClick={() => toggleExpand(index, "failed")}
                        className="jsonHeader"
                      >
                        <span
                          className={`toggleIcon ${
                            isExpanded ? "expanded" : "collapsed"
                          } failed`}
                        ></span>
                        <div className="jsonHeaderDetails">
                          <div className="details">
                            <strong>Base URL:</strong> {item.base_url}
                          </div>
                          <div className="details">
                            <strong>Endpoint:</strong> {item.endpoint}
                          </div>
                          <div className="details">
                            <strong>Method:</strong>{" "}
                            <span className="method">{item.method}</span>
                          </div>
                          <div
                            className={`jsonStatusCode ${getJsonStatusClass(
                              item.status_code
                            )}`}
                          >
                            Status Code: {item.status_code}
                          </div>
                        </div>
                      </div>
                      {isExpanded && (
                        <div className="jsonDetailsExpanded">
                          {requestBody && (
                            <pre className="jsonData">
                              <strong>
                                Request:
                                <br />
                              </strong>
                              <br />
                              {JSON.stringify(requestBody, null, 2)}
                            </pre>
                          )}
                          <pre className="jsonData">
                            <strong>
                              Response:
                              <br />
                            </strong>
                            <br />
                            {JSON.stringify(cleanedItem, null, 2)}
                          </pre>
                        </div>
                      )}
                    </div>
                  );
                })}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default FileUpload;
