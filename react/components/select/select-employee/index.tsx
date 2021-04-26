import React, { useMemo, forwardRef } from 'react';
import { Select, Tooltip } from 'choerodon-ui/pro';
import { useSelect, FlatSelect } from '@choerodon/components';
import { SelectConfig } from '@choerodon/components/lib/hooks/useSelect';
import { approveApi } from '@/api';
import { SelectProps } from 'choerodon-ui/pro/lib/select/Select';

export interface IEmployee {
  realName: string
  id: string
}

interface Props extends Partial<SelectProps> {
  flat?: boolean,
  request?: () => Promise<any>,
  selfEmpNum: string
  afterLoad?: (employees: IEmployee[]) => void,
  dataRef?: React.MutableRefObject<any>
}

const SelectEmployee: React.FC<Props> = forwardRef(({
  request, flat, selfEmpNum, afterLoad, dataRef, ...otherProps
}, ref: React.Ref<Select>) => {
  const config = useMemo((): SelectConfig => ({
    name: 'employee',
    textField: 'realName',
    valueField: 'id',
    request: ({ page, filter }) => {
      if (!request) {
        return approveApi.getEmployees(page, selfEmpNum, filter);
      }
      return request();
    },
    paging: true,
    middleWare: (data) => {
      if (dataRef) {
        Object.assign(dataRef, {
          current: data,
        });
      }
      if (afterLoad) {
        // @ts-ignore
        afterLoad(data);
      }
      return data;
    },
    optionRenderer: (item: IEmployee) => (
      <>

        <span>{`${item.realName}`}</span>
      </>
    ),
  }), [request, selfEmpNum]);
  const props = useSelect(config);
  const Component = flat ? FlatSelect : Select;
  return (
    <Component
      ref={ref}
      {...props}
      {...otherProps}
    />
  );
});
export default SelectEmployee;
