import React, { useEffect, useState, useCallback } from 'react';
import { unstable_batchedUpdates as batchedUpdates } from 'react-dom';
import { approveApi } from '@/api';
import { IApproveBtn } from '@/common/types';

interface ChildrenProps {
  loading: boolean,
  btns: IApproveBtn[]
}
interface Props {
  taskId: string
  children: (data: ChildrenProps) => React.ReactElement,
}

const useBtns = ({ taskId }: { taskId: string }): ChildrenProps => {
  const [loading, setLoading] = useState<boolean>(true);
  const [btns, setBtns] = useState<IApproveBtn[]>([]);

  const refresh = useCallback(async () => {
    setLoading(true);
    if (taskId) {
      const res = await approveApi.getProcess(taskId);
      batchedUpdates(() => {
        setBtns(res.approveActionList || []);
        setLoading(false);
      });
    }
  }, [taskId]);

  useEffect(() => {
    refresh();
  }, [refresh]);

  return {
    loading, btns,
  };
};

const Btns: React.FC<Props> = ({ taskId, children }) => {
  const data = useBtns({ taskId });
  return children(data);
};

export { Btns };
export default useBtns;
