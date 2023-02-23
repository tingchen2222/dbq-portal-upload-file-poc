import axios from 'axios';

const uploadApi = axios.create({
  baseURL: "http://localhost:8080",
});

export const uploadFiles = async (files) => {
  return uploadApi.post('/api/demo/upload', files);
}

export const downloadFile = async (fileKey) => {
  return uploadApi({
    url: `/api/demo/download/${fileKey}`,
    method: 'get',
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/pdf',
      'Content-Disposition': 'attachment; filename=${fileKey}',
    }
  })
}

export const getAllFiles = async () => {
  return uploadApi.get('/api/demo/allFiles');
}

export const deleteFileByKey = async (fileKey) => {
  return uploadApi.delete(`/api/demo/delete/${fileKey}`);
}