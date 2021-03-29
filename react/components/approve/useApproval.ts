import { ICommentTemplate, IProcess, IProcessAttachment } from '@/common/types';
import {
  useCallback, useEffect, useMemo, useState,
} from 'react';
import { usePersistFn } from 'ahooks';
import { getOrganizationId } from '@choerodon/agile/lib/utils/common';
import { approveApi } from '@/api';
import { SuggestProps } from '../suggest';

export interface Config {
  type: 'project' | 'org'
  process: IProcess
}
export default function useApproval(config: Config): SuggestProps {
  const { process, type } = config;
  const [attachmentUuid, setAttachmentUuid] = useState(process.attachmentUuid);
  useEffect(() => {
    if (process.attachmentUuid) {
      setAttachmentUuid(process.attachmentUuid);
    }
  }, [process.attachmentUuid]);
  const [commentTemplates, setCommentTemplates] = useState<ICommentTemplate[]>([]);
  const [processAttachments, setProcessAttachments] = useState<IProcessAttachment[]>([]);

  const [manageVisible, setManageVisible] = useState(false);
  const refresh = useCallback(async () => {
    const res = await approveApi.type(type).refreshCommentTemplates();
    setCommentTemplates(res);
  }, [type]);
  useEffect(() => {
    refresh();
  }, [refresh]);
  const refreshAttachments = useCallback(async () => {
    if (attachmentUuid) {
      const res = await approveApi.refreshProcessAttachment(attachmentUuid);
      setProcessAttachments(res);
    }
  }, [attachmentUuid]);
  useEffect(() => {
    refreshAttachments();
  }, [refreshAttachments]);
  const handleSaveCommentTemplate = usePersistFn(async (commentContent: string | ICommentTemplate) => {
    if (typeof commentContent === 'string') {
      await approveApi.type(type).saveCommentTemplates([{
        commentContent,
        tenantId: getOrganizationId(),
        enabledFlag: 1,
      }]);
    } else {
      await approveApi.saveCommentTemplates([commentContent]);
    }
    refresh();
  });
  const handleDeleteCommentTemplate = useCallback(async (commentTemplate: ICommentTemplate) => {
    await approveApi.type(type).deleteCommentTemplates(commentTemplate);
    refresh();
  }, [refresh, type]);
  const uploadProcessAttachment = useCallback(async (file: File) => {
    let uuid = attachmentUuid;
    if (!uuid) {
      uuid = (await approveApi.getUUID())?.content;
    }
    if (uuid) {
      await approveApi.AttachUpload({
        attachmentUuid: uuid,
        instanceId: process.taskDetail.instanceId,
        nodeId: process.taskDetail.nodeId,
        taskId: process.taskDetail.taskId,
        tenantId: process.taskDetail.tenantId,
      });
      const formData = new FormData();
      formData.append('file', file);
      formData.append('bucketName', 'hwkf');
      formData.append('directory', 'hwkf01');
      formData.append('attachmentUUID', uuid);
      await approveApi.uploadProcessAttachment(formData);
      setAttachmentUuid(uuid);
      await refreshAttachments();
    }
  }, [attachmentUuid, process.taskDetail.instanceId, process.taskDetail.nodeId, process.taskDetail.taskId, process.taskDetail.tenantId, refreshAttachments]);

  const deleteProcessAttachment = useCallback(async (url: string) => {
    await approveApi.deleteProcessAttachment(attachmentUuid!, [url]);
    await refreshAttachments();
  }, [attachmentUuid, refreshAttachments]);
  return useMemo(() => ({
    data: commentTemplates,
    onSaveCommentTemplate: handleSaveCommentTemplate,
    onDeleteCommentTemplate: handleDeleteCommentTemplate,
    manageVisible,
    setManageVisible,
    processAttachments,
    uploadProcessAttachment,
    deleteProcessAttachment,
  }), [commentTemplates, deleteProcessAttachment, handleDeleteCommentTemplate, handleSaveCommentTemplate, manageVisible, processAttachments, uploadProcessAttachment]);
}
