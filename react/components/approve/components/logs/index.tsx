import React from 'react';
import ApproveHistory from '@/components/history';
import { ProcessHistory } from '@/common/types';
import Section from '@/components/section';

interface Props {
  historyList: ProcessHistory[]
  isLoading?: boolean
}

const Logs: React.FC<Props> = ({ historyList, isLoading = false }) => (
  <Section key="taskLog" title="流程记录">
    <ApproveHistory historyList={historyList} isLoading={isLoading} />
  </Section>
);

export default Logs;
