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
import { ButtonColor, FuncType } from 'choerodon-ui/pro/lib/button/enum';
import { IApproveBtn } from '@/common/types';
import './index.less';

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
        openDelegateModal({ onClose: handleOk });
        break;
      }
      case 'ADD_SIGN': {
        // 打开加签弹窗
        openAddApproverModal({ onSuccess: handleOk });
        break;
      }
      case 'CARBON_COPY': {
        openCCModal({});
        break;
      }
      case 'APPOINT_NEXT_NODE_APPROVER': {
        approveApi.forecastNextNode(taskId).then((res: any) => {
          if (res.failed) {
            Choerodon.prompt(res.message);
          } else {
            openNextApproveModal({
              forecastNextNode: res, onClose: handleOk,
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
          } else if (res.startNode || res.previousNode) {
            openRebutModal({
              rebutNodeList: res, onClose: handleOk,
            });
          } else {
            Choerodon.prompt('没有可驳回的节点，不可驳回');
          }
        });
        break;
      }
      case 'ADD_TASK_APPROVER': {
        openAddApproveModal({ onClose: handleOk });
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
    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '10px' }}>
      {
        outLoading || loading ? null : (
          <>
            {[...extraBtns, ...btns].filter((item) => item.checked).map((item) => (
              <Button
                style={{ margin: 0, ...buttonStyle, ...((item as ICustomBtn).style || {}) }}
                className={(item as ICustomBtn).className}
                key={item.value}
                onClick={() => handleClickBtn(item)}
                // color={'blue' as ButtonColor}
                funcType={'raised' as FuncType}
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
