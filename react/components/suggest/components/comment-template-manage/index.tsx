import React from 'react';
import { observer } from 'mobx-react-lite';
import { usePersistFn } from 'ahooks';
import ManageList from '@/components/ManageList';
import { ICommentTemplate } from '@/common/types';

export interface CommentTemplateManageProps {
  fixed?: boolean
  data: ICommentTemplate[]
  manageVisible: boolean
  setManageVisible: (visible: boolean) => void
  onSaveCommentTemplate: (commentContent: string) => void
  onDeleteCommentTemplate: (commentContent: ICommentTemplate) => void
}
const CommentTemplateManage: React.FC<CommentTemplateManageProps> = ({
  data: commentTemplates,
  manageVisible,
  setManageVisible,
  onSaveCommentTemplate,
  onDeleteCommentTemplate,
  fixed,
}) => {
  const handleSubmit = usePersistFn((oldData, newValue) => {
    onSaveCommentTemplate({ ...oldData, commentContent: newValue });
  });

  const handleDelete = usePersistFn(async (data: ICommentTemplate) => {
    onDeleteCommentTemplate(data);
  });
  return (
    <div>
      <ManageList
        fixed={fixed}
        title="审批意见管理"
        visible={manageVisible}
        setVisible={(value) => {
          setManageVisible(value);
        }}
        data={commentTemplates}
        textField="commentContent"
        valueField="commentTemplateId"
        onDelete={handleDelete}
        onSubmit={handleSubmit}
      />
    </div>
  );
};

export default observer(CommentTemplateManage);
