import React, { forwardRef, useMemo } from 'react';
import { Select } from 'choerodon-ui/pro';
import { FlatSelect, useSelect } from '@choerodon/components';
import { SelectConfig } from '@choerodon/components/lib/hooks/useSelect';
import { approveApi, IWorkflowUser } from '@/api';
import { SelectProps } from 'choerodon-ui/pro/lib/select/Select';

interface Props extends Partial<SelectProps> {
  flat?: boolean,
  request?: () => Promise<any>,
  selfUserId: number | string
  afterLoad?: (employees: IWorkflowUser[]) => void,
  dataRef?: React.MutableRefObject<any>
}

const SelectWorkflowUser: React.FC<Props> = forwardRef(({
  name, request, flat, selfUserId, afterLoad, dataRef, multiple=false, label, ...otherProps
}, ref: React.Ref<Select>) => {
  const config = useMemo((): SelectConfig => ({
    name,
    textField: 'realName',
    valueField: 'id',
    request: ({ page, filter }) => {
      if (!request) {
        return approveApi.getEmployees(page, selfUserId, filter);
      }
      return request();
    },
    paging: true,
    middleWare: (data) => {
      if (afterLoad) {
        // @ts-ignore
        afterLoad(data);
      }
      return data;
    },
    optionRenderer: (item: IWorkflowUser) => (
      <>

        <span>{`${item.realName}`}</span>
      </>
    ),
  }), [request, selfUserId]);
  const props = Object.assign(useSelect(config), {
    onChange: (value : any, oldValue: any) : void => {
      if(dataRef) {
         Object.assign(dataRef, {current: value});
      }
    },
    primitiveValue: false,
    multiple,
    label
  });
  const Component = flat ? FlatSelect : Select;
  return (
    <Component
      ref={ref}
      {...props}
      {...otherProps}
    />
  );
});
export default SelectWorkflowUser;
