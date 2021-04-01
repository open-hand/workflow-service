import React, { useCallback, useEffect, useState } from 'react';
import { ApproveLog, IHistoryAttachment } from '@/common/types';
import FileSaver from 'file-saver';
import './HistoryItem.less';
import { approveApi } from '@/api';

const prefix = 'c7n-backlogApprove-historyItem';

interface LogProps {
  log: ApproveLog,
}
const HistoryItem: React.FC<LogProps> = ({ log }) => {
  const {
    nodeType, nodeName, statusMeaning, commentContent, remark, attachmentUuid, assignee, carbonCopyComment,
  } = log;

  const [attachmentList, setAttachmentList] = useState<IHistoryAttachment[]>([]);

  useEffect(() => {
    const getAttachmentList = async () => {
      if (attachmentUuid) {
        const res = await approveApi.getAttachments(attachmentUuid);
        setAttachmentList(res);
      }
    };
    getAttachmentList();
  }, [attachmentUuid]);

  const handleDownLoad = useCallback((item) => {
    if (item.fileUrl) {
      FileSaver.saveAs(item.fileUrl, item.fileName);
    }
  }, []);

  return (
    <div className={prefix}>
      {
        nodeType === 'startNode' && (
          <span>审核流程开始</span>
        )
      }
      {
        nodeType === 'endNode' && (
          <span>审核流程结束</span>
        )
      }
      {
        nodeType !== 'startNode' && nodeType !== 'endNode' && (
          <>
            <span
              className={`${prefix}-value`}
              style={{
                marginRight: 5,
              }}
            >
              {`${assignee ?? ''}`}
            </span>
            <span>在</span>
            <span className={`${prefix}-value`}>{`【${nodeName}】`}</span>
            <span>操作审批动作为</span>
            <span className={`${prefix}-value`}>{`【${statusMeaning}】`}</span>
            {
              carbonCopyComment && (
                <>
                  <span>填写评论</span>
                  <span className={`${prefix}-value`}>{`【${carbonCopyComment}】`}</span>
                </>
              )
            }
            {
              commentContent && (
              <>
                <span>填写审批意见</span>
                <span className={`${prefix}-value`}>{`【${commentContent}】`}</span>
              </>
              )
            }
            {
              remark && (
                <>
                  <span>填写备注</span>
                  <span className={`${prefix}-value`}>{`【${remark}】`}</span>
                </>
              )
            }
            {
              attachmentUuid && attachmentList?.length > 0 && (
                <>
                  <span>上传附件</span>
                  {/* <span className={`${prefix}-value`}>{`【${attachmentUuid}】`}</span> */}
                  <span className={`${prefix}-value`}>【</span>
                  {
                    attachmentList.map((item, i, arr) => (
                      <>
                        <span
                          className={`${prefix}-value`}
                          onClick={() => handleDownLoad(item)}
                          role="none"
                          style={{
                            cursor: 'pointer',
                          }}
                        >
                          {item.fileName}
                        </span>
                        {
                          i < arr.length - 1 && (
                            <span
                              className={`${prefix}-value`}
                            >
                              、
                            </span>
                          )
                        }
                      </>
                    ))
                  }
                  <span className={`${prefix}-value`}>】</span>
                </>
              )
            }
          </>
        )
      }
    </div>
  );
};

export default HistoryItem;
