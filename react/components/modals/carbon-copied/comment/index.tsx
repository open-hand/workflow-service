import React, { useCallback, useMemo } from 'react';
import {
  Form, TextArea, DataSet, Button,
} from 'choerodon-ui/pro';
import { approveApi } from '@/api';
import { ButtonColor, FuncType } from 'choerodon-ui/pro/lib/button/enum';
import Part from '@/components/section';

interface CommentProps {
  onSubmit: () => void
  taskHistoryId: string
}
const Comment: React.FC<CommentProps> = ({ taskHistoryId, onSubmit }) => {
  const dataSet = useMemo(() => new DataSet({
    autoCreate: true,
    fields: [{
      label: '评论意见',
      name: 'comment',
      required: true,
    }],
  }), []);
  const handleSubmit = useCallback(async () => {
    if (await dataSet.validate()) {
      const comment = dataSet.current?.get('comment');
      await approveApi.comment(comment, taskHistoryId);
      onSubmit();
    }
  }, [dataSet, onSubmit, taskHistoryId]);
  return (
    <div style={{ padding: '0 20px' }}>
      <Part
        title="评论意见"
      >
        <Form dataSet={dataSet} style={{ width: '100%' }}>
          <TextArea name="comment" />
          <div>
            <Button
              onClick={handleSubmit}
              color={'blue' as ButtonColor}
              funcType={'raised' as FuncType}
            >
              评论
            </Button>
          </div>
        </Form>
      </Part>
    </div>
  );
};

export default Comment;
