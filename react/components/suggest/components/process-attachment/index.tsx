import React, { useCallback, useEffect, useState } from 'react';
import { observer } from 'mobx-react-lite';
import { Upload } from 'choerodon-ui';
import { Button } from 'choerodon-ui/pro';
import SingleFileUpload from '@choerodon/agile/lib/components/SingleFileUpload';
import { IProcessAttachment } from '@/common/types';
import { ButtonColor } from 'choerodon-ui/pro/lib/button/enum';
import validateFile from '@choerodon/agile/lib/utils/File';

export interface FileItem {
  uid: string,
  name: string,
  url: string,
  userId: string,
  percent?: number,
  status?: string
}
export interface ProcessAttachmentProps {
  processAttachments: IProcessAttachment[]
  uploadProcessAttachment: (file: File) => void
  deleteProcessAttachment: (url: string) => void
}
const ProcessAttachment: React.FC<ProcessAttachmentProps> = ({
  processAttachments,
  uploadProcessAttachment,
  deleteProcessAttachment,
}) => {
  const [fileList, setFileList] = useState<FileItem[]>([]);

  useEffect(() => {
    const initialFileList: FileItem[] = (processAttachments || []).map((attachment) => ({
      uid: attachment.creationDate,
      name: attachment.fileName,
      url: attachment.fileUrl,
      userId: attachment.createdBy,
    }));
    setFileList(initialFileList);
  }, [processAttachments]);
  const handleFileUpload = useCallback((option) => {
    if (validateFile([option.file])) {
      uploadProcessAttachment(option.file);
    }
  }, [uploadProcessAttachment]);
  const handleDeleteFile = useCallback((item: FileItem) => {
    deleteProcessAttachment(item.url);
  }, [deleteProcessAttachment]);

  return (
    <div>
      <Upload customRequest={handleFileUpload} showUploadList={false}>
        <Button icon="backup-o">上传附件</Button>
      </Upload>
      {
        fileList && fileList.length > 0 && (
          <div style={{ display: 'flex', flexWrap: 'wrap', marginTop: 10 }}>
            {fileList.map((item) => (
              <SingleFileUpload
                key={item.uid}
                url={item.url}
                fileName={item.name}
                hasDeletePermission
                percent={!item.url && (item.percent || 0)}
                error={!item.url && item.status === 'error'}
                onDeleteFile={() => {
                  handleDeleteFile(item);
                }}
              />
            ))}
          </div>
        )
      }
    </div>
  );
};

export default observer(ProcessAttachment);
