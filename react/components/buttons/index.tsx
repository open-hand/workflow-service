import React, { useCallback } from 'react';
import { Choerodon } from '@choerodon/boot';
import useBtns from '@/hooks/useBtns';
import { Button } from 'choerodon-ui/pro';
import { approveApi } from '@/api';
import openCCModal from '@/components/modals/components/cc-modal';
import openDelegateModal from '@/components/modals/components/delegate-modal';
import openNextApproveModal from '@/components/modals/components/nextNode-approve-modal';
import openRebutModal from '@/components/modals/components/rebut-modal';
import openAddApproveModal from '@/components/modals/components/add-approve/AddApproveModal';
import openAddApproverModal from '@/components/modals/components/addApprover';
import { IApproveBtn } from '@/common/types';
import './index.less';
import classNames from 'classnames';
import { isArray } from "lodash";

export interface ICustomBtn {
  checked: boolean,
  value: string,
  meaning: string
  onClick: () => void,
  style?: object,
  className?: string
}
export interface ButtonsProps {
  taskId: string
  getComment: () => Promise<boolean | string>
  onSubmit: () => void
  onClick?: (item: IApproveBtn | ICustomBtn) => boolean
  extraBtns?: ICustomBtn[]
  outLoading?: boolean
  buttonStyle?: object
}
const Buttons: React.FC<ButtonsProps> = ({
  taskId, getComment, onSubmit, onClick, extraBtns = [], outLoading = false, buttonStyle = {},
}) => {
  const { btns, loading } = useBtns({ taskId });
  const handleOk = useCallback(() => {
    onSubmit();
  }, [onSubmit]);
  const handleClickBtn = useCallback(async (item: IApproveBtn | ICustomBtn) => {
    if (onClick && !onClick(item)) {
      return;
    }
    if ((item as ICustomBtn).onClick && typeof (item as ICustomBtn).onClick === 'function') {
      (item as ICustomBtn).onClick();
      return;
    }
    switch (item.value) {
      case 'APPROVED': {
        const comment = await getComment();
        if (comment && typeof comment === 'string') {
          await approveApi.approved(taskId, comment);
          handleOk();
        }
        break;
      }
      case 'REJECTED': {
        // 校验建议
        const comment = await getComment();
        if (comment && typeof comment === 'string') {
          await approveApi.reject(taskId, comment);
          handleOk();
        }
        break;
      }
      case 'DELEGATE': {
        openDelegateModal({ taskId, onClose: handleOk });
        break;
      }
      case 'ADD_SIGN': {
        // 打开加签弹窗
        openAddApproverModal({ taskId, onSuccess: handleOk });
        break;
      }
      case 'CARBON_COPY': {
        openCCModal({ taskId });
        break;
      }
      case 'APPOINT_NEXT_NODE_APPROVER': {
        approveApi.forecastNextNode(taskId).then((res: any) => {
          if (res.failed) {
            Choerodon.prompt(res.message);
          } else {
            openNextApproveModal({
              forecastNextNode: res, onClose: handleOk, taskId,
            });
          }
        });
        break;
      }
      case 'REBUT': {
        // 驳回
        approveApi.rebutNodeList(taskId).then((res: any) => {
          if (res.failed) {
            Choerodon.prompt(res.message);
          } else if (isArray(res) && res.length > 0) {
            openRebutModal({
              rebutNodeList: res, onClose: handleOk, taskId,
            });
          } else {
            Choerodon.prompt('没有可驳回的节点，不可驳回');
          }
        });
        break;
      }
      case 'ADD_TASK_APPROVER': {
        openAddApproveModal({ onClose: handleOk, taskId });
        break;
      }
      default: {
        // 自定义按钮动作
        await approveApi.customAction(taskId, item.value);
        handleOk();
        break;
      }
    }
  }, [getComment, handleOk, onClick, taskId]);
  return (
    <div style={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'flex-end' }}>
      {
        outLoading || loading ? null : (
          <>
            {[...btns, ...extraBtns].filter((item) => item.checked).map((item) => (
              <Button
                style={{ ...buttonStyle, ...((item as ICustomBtn).style || {}) }}
                className={classNames((item as ICustomBtn).className, 'c7n-approve-btn')}
                key={item.value}
                onClick={() => handleClickBtn(item)}
              >
                {item.meaning}
              </Button>
            ))}
          </>
        )
      }
    </div>
  );
};

export default Buttons;
