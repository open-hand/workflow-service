import React from 'react';
import { observer } from 'mobx-react-lite';
import useApproval from '@/components/approve/useApproval';
import Suggest, { SuggestRef } from '@/components/suggest';
import Part from '@/components/section';
import ProcessDetail from '../process-detail';
import store from '../../store';

const ApprovalSuggest: React.FC<{
  innerRef: React.MutableRefObject<SuggestRef>
}> = ({ innerRef }) => {
  const data = store.process;
  const props = useApproval({ type: 'org', process: data });
  return (
    <div>
      <ProcessDetail />
      <Part
        title="审批意见"
      >
        <Suggest {...props} ref={innerRef} />
      </Part>
    </div>
  );
};

export default observer(ApprovalSuggest);
