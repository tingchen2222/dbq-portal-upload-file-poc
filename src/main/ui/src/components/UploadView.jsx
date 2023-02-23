import React, {useState, useEffect} from "react";
import {deleteFileByKey, downloadFile, getAllFiles, uploadFiles} from "../api/uploadApi";

const UploadView = () => {

  const [files, setFiles] = useState([]);
  const [fileList, setFileList] = useState([]);

  useEffect(() => {
    getAllFiles().then(response => {
      setFileList(response.data);
    }).catch(err => {
      console.log(err);
    })
  }, []);

  const handleFileUpload = (e) => {
    const selectedFiles = Array.from(e.target.files);
    setFiles(selectedFiles);
  }

  const handleFileSubmit =  async (e) => {
    e.preventDefault();
    const formData = new FormData();

    files.forEach(file => {
      formData.append("files", file);
    });

    for (let pair of formData.values()) {
        console.log(pair);
    }

    const fileNames = files.map(file => file.name);
    // upload file and also update the fileList with the new uploaded files

    //  todo: maybe need async for this? since files r being added to list before the request is done
    uploadFiles(formData).then(setFileList([...fileList, ...fileNames]));
  }

  const handleFileDelete = (name) => {
    //delete file and also update the fileList to remove the deleted file
    deleteFileByKey(name).then(setFileList(fileList.filter(file => file != name)));
  }

  const handleFileDownload = (fileKey) => {
    downloadFile(fileKey).then(res => {
      const fileUrl = URL.createObjectURL(res.data);
      window.location.href = fileUrl;
    });
  }

  // display all the files that will be uploaded
  const listUploadFiles = files.map(fileName =>
    <li key={fileName.toString()}>
      {fileName.name}
    </li>
  );

  // display all the files in the s3Bucket
  const listS3Files = fileList.map((fileName) =>
    <li key={fileName.toString()}>
      {fileName}
      <button onClick={() => {handleFileDownload(fileName)}}>Download</button>
      <button onClick={() => {handleFileDelete(fileName)}}>Delete</button>
    </li>
  );

  return (
    <>
      <form encType="multipart/form-data" onSubmit={handleFileSubmit}>
        <label>Select files to upload:
          <input type="file" name="files" multiple onChange={handleFileUpload}/>
        </label>
        <button type="submit">Upload Files </button>
      </form>
      <h1>FILES BEING UPLOADED</h1>
      <ul>
        {listUploadFiles}
      </ul>
      <h1>FILES IN S3 BUCKET</h1>
      <ul>
        {listS3Files}
      </ul>
    </>
  )
}

export default UploadView