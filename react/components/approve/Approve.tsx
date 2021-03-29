import React, {
  useCallback, useEffect, useMemo, useRef,
} from 'react';
import { observer } from 'mobx-react-lite';
import Suggest, { SuggestRef } from '@/components/suggest';
import Buttons from '@/components/buttons';
import store from '@/components/modals/store';
import ApproveContext from './context';
import styles from './Approve.less';
import useApproval from './useApproval';

interface ApproveProps {
  businessKey: string
  taskId: string
  onApprove: () => void
}

const Approve: React.FC<ApproveProps> = ({
  businessKey, taskId, onApprove,
}) => {
  const contextValue = useMemo(() => ({}), []);
  const props = useApproval({ type: 'project', process: store.process });
  const ref = useRef<SuggestRef>({} as SuggestRef);
  useEffect(() => {
    if (taskId) {
      store.getProcess(taskId, 'project');
    }
  }, [taskId]);
  const getComment = useCallback(async () => {
    const dataSet = ref.current?.dataSet;
    if (dataSet && await dataSet.validate()) {
      return dataSet.current?.get('commentContent');
    }
    return false;
  }, []);
  const handleSubmit = useCallback(() => {
    onApprove();
  }, [onApprove]);
  return (
    <ApproveContext.Provider value={contextValue}>
      <div className={styles.approve}>
        <Suggest layout="detail" {...props} ref={ref} />
        <Buttons
          taskId={taskId}
          getComment={getComment}
          onSubmit={handleSubmit}
        />
      </div>
    </ApproveContext.Provider>
  );
};

export default observer(Approve);
